/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;
/**
 *
 * @author Annabelle
 */
public class Communication_serveur extends Thread {
    private int port;
    private ServerSocket socket_serveur;
    private boolean continuer;
    
    public Communication_serveur(int port)
    {
        this.port = port;
        this.continuer = true;
    }
    
    public void run()
    {
        try 
        {
            this.socket_serveur = new ServerSocket(this.port);
            //On attend les connexions
            while(this.continuer)
            {
                Thread t = new Thread(new Accepter_client(this.socket_serveur.accept()));
                t.start();
            }
            this.socket_serveur.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}

class Accepter_client implements Runnable {
    private Socket socket;
    private ObjectInputStream dis;
    private ObjectOutputStream dos;
    private Parametres parametres;
    private Communication communication;
    
    public Accepter_client(Socket s)
    {
        this.socket = s;
        this.parametres = new Parametres();
        this.communication = new Communication();
    }

    public void run() 
    {
        int choix_client;
        try 
        {
            //Mise en place des canaux de communication
            this.dis = new ObjectInputStream(this.socket.getInputStream());
            this.dos = new ObjectOutputStream(this.socket.getOutputStream());
            
            //Réception de la demande du client
            choix_client = this.dis.readInt();
            
            //Exécution de la demande du client
            switch(choix_client)
            {
                //Réception des schémas
                case 0 : this.reception_schemas(); break;
                //Réception des schémas du programme P
                case 1 : this.reception_schemas_programme(); break;
                //Réception requête BD
                case 2 : this.executer_requete(); break;
            }
            
            //Fermeture du socket
            this.dis.close();
            this.dos.close();
            this.socket.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private void reception_fichier(String chemin_fichier)
    {
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File(chemin_fichier));
            //Réception du fichier
            int n;
            String contenu = this.dis.readUTF();
            out.write(contenu);
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }  
    }
    
    private void copier_fichier(File source, File dest)
    {
        FileInputStream is = null;
        FileWriter os = null;
        try 
        {
            is = new FileInputStream(source);
        
            os = new FileWriter(dest);
            int n;
            String contenu = "";
            int taille = is.available();
            byte[] buffer = new byte[1024];
            while((n=is.read(buffer))!=-1)
                contenu += new String(buffer);
            os.write(contenu.substring(0, taille));
            is.close();
            os.close();
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void reception_schemas()
    {
        //Réception du schéma local
        this.reception_fichier(this.parametres.getChemin_schemas()+"/local.json");
        System.out.println("Schéma local reçu.");
        
        //Réception du schéma global
        this.reception_fichier(this.parametres.getChemin_schemas()+"/global_nouveau.json");
        System.out.println("Schéma global reçu.");
        
        //Mise à jour de la BD
        this.construction_BD();
        
        //MAJ du schéma global
        File source = new File(this.parametres.getChemin_schemas()+"/global_nouveau.json");
        File dest = new File(this.parametres.getChemin_schemas()+"/global.json");
        this.copier_fichier(source, dest);
    }
    
    private void reception_schemas_programme()
    {
        String schemas_a_envoyer = this.parametres.getSchemas_a_envoyer();
        //Réception du schéma global
        this.reception_fichier(schemas_a_envoyer+"/global.json");
        System.out.println("Schéma global reçu.");
        //Réception des schémas locaux
        //Réception du nombre de schémas locaux
        int nb_serveurs = 0;
        try {
            nb_serveurs = this.dis.readInt();
        } catch (IOException ex) {
            Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
        }
        int num_schema = 0;
        for(int i=0; i<nb_serveurs; i++)
        {
            //Réception du numéro de schéma local
            try {
                num_schema = this.dis.readInt();
            } catch (IOException ex) {
                Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.reception_fichier(schemas_a_envoyer+"/local_"+num_schema+".json");
            System.out.println("Schéma local "+num_schema+" reçu.");
        }
        
        //Récupération des schémas du serveur   
        String chemin_schemas = this.parametres.getChemin_schemas();
        int num_local = this.parametres.getNum_serveur();
        File source = new File(schemas_a_envoyer+"/global.json");
        File dest = new File(chemin_schemas+"/global.json");
        this.copier_fichier(source, dest);
        source = new File(schemas_a_envoyer+"/local_"+num_local+".json");
        dest = new File(chemin_schemas+"/local.json");
        this.copier_fichier(source, dest);
        
        //Envoi des schémas aux autres serveurs
        this.communication.envoi_schemas();   
    }
    
    private void executer_requete()
    {
        String tables = "";
        String attributs = "";
        String conditions = "";
        
        //Récupération des éléments de la requête
        try 
        {
            tables = this.dis.readUTF();
            attributs = this.dis.readUTF();
            conditions = this.dis.readUTF();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Exécution de la requête
        Communication_BD com_BD = new Communication_BD(this.parametres.getBD_login(), this.parametres.getBD_mdp(), false);
        
        //Envoi du résultat
        try 
        {
            this.dos.writeObject((Object)com_BD.requete(tables, attributs, conditions));
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void construction_BD()
    {
        Schema_local bd_actuelle = new Schema_local(true);
        Schema_local bd_nouvelle = new Schema_local(false);
        //TODO: automatisation pour savoir si la co se fait à la fac ou non
        Communication_BD com_BD = new Communication_BD(this.parametres.getBD_login(), this.parametres.getBD_mdp(), false);
        
        //Construction des tables qui n'existent pas
        System.out.println("/***Construction des tables***/");
        String[] tables_actuelles = bd_actuelle.get_liste_nom_tables();
        String[] tables_nouvelles = bd_nouvelle.get_liste_nom_tables();
        String[] attributs_nouveaux;
        String creation_attributs;
        String creation_pk;
        int taille_creation_pk;
        boolean creation;
        for(int i=0; i<tables_nouvelles.length; i++)
        {
            creation = true;
            creation_attributs = "";
            creation_pk = "PRIMARY KEY(";
            taille_creation_pk = creation_pk.length();
            for(int j=0; j<tables_actuelles.length; j++)
                if(tables_nouvelles[i].equals(tables_actuelles[j]))
                {
                    creation = false;
                    j = tables_actuelles.length;
                }
            if(creation)
            {
                attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables_nouvelles[i]);
                for(int j=0; j<attributs_nouveaux.length; j++)
                {
                    creation_attributs += attributs_nouveaux[j]+" "+bd_nouvelle.get_type_attribut(tables_nouvelles[i], attributs_nouveaux[j])+", ";
                    if(bd_nouvelle.is_primary_key(tables_nouvelles[i], attributs_nouveaux[j]))
                    {
                        if(creation_pk.length()!=taille_creation_pk)
                            creation_pk += ", ";
                        creation_pk += attributs_nouveaux[j];
                    }
                }
                creation_attributs += creation_pk+")";
                System.out.println("Création de la table "+tables_nouvelles[i]+".");
                System.out.println(creation_attributs);
                //com_BD.ajoutTable(tables_nouvelles[i], null);
            }
        }
        
        //Construction des colonnes qui n'existent pas
        System.out.println("/***Construction des colonnes***/");
        String[] attributs_actuels;
        for(int i=0; i<tables_nouvelles.length; i++)
        {
            creation = false;
            for(int j=0; j<tables_actuelles.length; j++)
                if(tables_nouvelles[i].equals(tables_actuelles[j]))
                {
                    creation = true;
                    j = tables_actuelles.length;
                }
            if(creation)
            {
                attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables_nouvelles[i]);
                attributs_actuels = bd_actuelle.get_liste_attributs_table(tables_nouvelles[i]);
                for(int j=0; j<attributs_nouveaux.length; j++)
                {
                    creation = true;
                    for(int k=0; k<attributs_actuels.length; k++)
                        if(attributs_nouveaux[j].equals(attributs_actuels[k]))
                        {
                            creation = false;
                            k = attributs_actuels.length;
                        }
                    if(creation)
                    {
                        System.out.println("Ajout de la colonne "+attributs_nouveaux[j]+" type "+bd_nouvelle.get_type_attribut(tables_nouvelles[i], attributs_nouveaux[j])+" à la table "+tables_nouvelles[i]+".");
                        //com_BD.ajoutColonne(tables_nouvelles[i], attributs_nouveaux[j], bd_nouvelle.get_type_attribut(tables_nouvelles[i], attributs_nouveaux[j]));
                    }
                }
            }
        }
        
        //Récupération des tuples manquants
        System.out.println("/***Récupération des tuples***/");
        Schema_global bd_globale = new Schema_global();
        String tables;
        String attributs;
        String conditions;
        int[] num_serveurs;
        int num_serveur_envoi_requete;
        String[][] tab_conditions;
        for(int i=0; i<tables_nouvelles.length; i++)
        {   
            attributs = "";
            tab_conditions = null;
            conditions = "";
            //Recherche des tuples souhaités
            tables = tables_nouvelles[i];
            attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables);
            //Définition des conditions
            if(!bd_nouvelle.get_table_fragmentation(tables_nouvelles[i]).equals("verticale"))
                tab_conditions = bd_nouvelle.get_attributs_fragment(tables, 0);
            else
                conditions = "1=1";
            
            if(bd_globale.get_table_fragmentation(tables_nouvelles[i]).equals("horizontale"))
            {
                //Fragmentation horizontale
                //Récupération des serveurs sur lesquels il y a des fragments
                ArrayList<Integer> liste_serveurs = new ArrayList<>();
                liste_serveurs.clear();
                int[] tab_serveurs_par_fragment;
                int nb_fragments = bd_globale.get_nb_fragments(tables);
                for(int j=0; j<nb_fragments; j++)
                {
                    tab_serveurs_par_fragment = bd_globale.get_serveurs_fragment(tables, j);
                    for(int k=0; k<tab_serveurs_par_fragment.length; k++)
                        if(tab_serveurs_par_fragment[k]!=this.parametres.getNum_serveur() && !liste_serveurs.contains(tab_serveurs_par_fragment[k]))
                            liste_serveurs.add(tab_serveurs_par_fragment[k]);
                }
                //Construction de la requête
                if(liste_serveurs.size()>0)
                {
                    //Attributs
                    for(int j=0; j<attributs_nouveaux.length; j++)
                    {
                        if(!attributs.equals(""))
                            attributs += ", ";
                        attributs += attributs_nouveaux[j];
                    }
                    //Conditions
                    for(int j=0; j<tab_conditions.length; j++)
                    {
                        if(!conditions.equals(""))
                            conditions += " AND ";
                        conditions += tab_conditions[j][0]+""+tab_conditions[j][1]+""+tab_conditions[j][2];
                    }
                    //Envoi de la requête aux serveurs
                    for(int j=0; j<liste_serveurs.size(); j++)
                    {
                        System.out.println("Requete au serveur "+liste_serveurs.get(j)+" : Table "+tables+", attributs : "+attributs);
                        System.out.println("Conditions : "+conditions);
                        /*com_BD.ajoutTuples(this.communication.envoi_requete(liste_serveurs.get(j), tables, attributs, conditions), 
                                tables, bd_nouvelle.get_cles_primaires(tables));*/
                    }
                }
            }
            if(!bd_globale.get_table_fragmentation(tables_nouvelles[i]).equals("horizontale"))
            {
                //Fragmentation verticale et hybride
                //On vérifie tous les serveurs pour savoir auxquels demander des tuples
                for(int j=0; j<this.parametres.getNb_serveurs(); j++)
                {
                    attributs = "";
                    num_serveur_envoi_requete = this.parametres.getNum_serveur_distant(j);
                    if(num_serveur_envoi_requete!=this.parametres.getNum_serveur())
                    {
                        //On parcourt tous les attributs qui seront dans la BD mise à jour
                        for(int k=0; k<attributs_nouveaux.length; k++)
                        {
                            num_serveurs = bd_globale.get_num_serveurs(tables_nouvelles[i], attributs_nouveaux[k]);
                            //Si l'attribut est sur le serveur, on lui demande les tuples
                            for(int l=0; l<num_serveurs.length; l++)
                            {
                                if(num_serveur_envoi_requete==num_serveurs[l])
                                {
                                    //MAJ attributs
                                    if(!attributs.equals(""))
                                        attributs += ", ";
                                    attributs += attributs_nouveaux[k];
                                    //MAJ conditions
                                    if(tab_conditions!=null)
                                    {
                                        for(int m=0; m<tab_conditions.length; m++)
                                        {
                                            if(tab_conditions[m][0].equals(attributs_nouveaux[k]))
                                            {
                                                if(!conditions.equals(""))
                                                    conditions += " AND ";
                                                conditions += tab_conditions[m][0]+""+tab_conditions[m][1]+""+tab_conditions[m][2];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //Si le serveur a des tuples concernés, on lui envoie une requête
                        if(!attributs.equals(""))
                        {
                            System.out.println("Requete au serveur "+num_serveur_envoi_requete+" : Table "+tables+", attributs : "+attributs);
                            System.out.println("Conditions : "+conditions);
                            /*com_BD.ajoutTuples(this.communication.envoi_requete(num_serveur_envoi_requete, tables, attributs, conditions), 
                                    tables, bd_nouvelle.get_cles_primaires(tables));*/
                        }
                    }
                }
            }
        }
        
        //Attente de la confirmation des autres serveurs
        System.out.println("/***Attente des la confirmations des autres serveurs avant la suppression***/");
        //Sotckage des réponses dans un fichier pour y avoir accès sans bloquer le programme ?
        
        //Suppression des tuples (pour la fragmentation horizontale seulement)
        System.out.println("/***Suppression des tuples***/");
        
        //Suppression des colonnes
        System.out.println("/***Suppression des colonnes***/");
        boolean suppression;
        for(int i=0; i<tables_nouvelles.length; i++)
        {
            suppression = false;
            for(int j=0; j<tables_actuelles.length; j++)
                if(tables_nouvelles[i].equals(tables_actuelles[j]))
                {
                    suppression = true;
                    j = tables_actuelles.length;
                }
            if(suppression)
            {
                attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables_nouvelles[i]);
                attributs_actuels = bd_actuelle.get_liste_attributs_table(tables_nouvelles[i]);
                for(int j=0; j<attributs_actuels.length; j++)
                {
                    suppression = true;
                    for(int k=0; k<attributs_nouveaux.length; k++)
                        if(attributs_actuels[j].equals(attributs_nouveaux[k]))
                        {
                            suppression = false;
                            k = attributs_actuels.length;
                        }
                    if(suppression)
                    {
                        System.out.println("Suppression de la colonne "+attributs_nouveaux[j]+" de la table "+tables_nouvelles[i]+".");
                        //com_BD.suppressionColonne(tables_nouvelles[i], attributs_nouveaux[j]);
                    }
                }
            }
        }
        
        //Suppression des tables
        System.out.println("/***Suppression des tables***/");
        for(int i=0; i<tables_actuelles.length; i++)
        {
            suppression = true;
            for(int j=0; j<tables_nouvelles.length; j++)
                if(tables_actuelles[i].equals(tables_nouvelles[j]))
                {
                    suppression = false;
                    j = tables_nouvelles.length;
                }
            if(suppression)
            {
                System.out.println("Suppression de la table "+tables_actuelles[i]+".");
                //com_BD.suppressionTable(tables_nouvelles[i]);
            }
        }
        
        //MAJ du fichier BD_actuelle.json
        File source = new File(this.parametres.getChemin_schemas()+"/local.json");
        File dest = new File(this.parametres.getChemin_schemas()+"/BD_actuelle.json");
        this.copier_fichier(source, dest);
    }
}
