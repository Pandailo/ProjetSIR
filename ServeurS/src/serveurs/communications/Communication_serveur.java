/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs.communications;

import java.io.*;
import static java.lang.Thread.sleep;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import serveurs.Parametres;
import serveurs.Schema_global;
import serveurs.Schema_local;
/**
 *
 * @author Annabelle
 */
public class Communication_serveur extends Thread {
    private int port;
    private ServerSocket socket_serveur;
    private boolean continuer;
    private boolean[] MAJ_BD;
    
    public Communication_serveur(int port)
    {
        this.port = port;
        this.continuer = true;
        this.initialisation_MAJ_BD();
    }
    
    public void initialisation_MAJ_BD()
    {
        Parametres p = new Parametres();
        int nb_serveurs = p.getNb_serveurs();
        this.MAJ_BD = new boolean[nb_serveurs];
        for(int i=0; i<nb_serveurs; i++)
            this.MAJ_BD[i] = false;
    }
    
    public boolean bd_dispo()
    {
        boolean res = true;
        for(int i=0; i<this.MAJ_BD.length; i++)
            if(!this.MAJ_BD[i])
            {
                res = false;
                i=this.MAJ_BD.length;
            }
        return res;
    }
    
    public void bd_maj_dispo()
    {
        for(int i=0; i<this.MAJ_BD.length; i++)
            if(!this.MAJ_BD[i])
            {
                this.MAJ_BD[i] = true;
                i=this.MAJ_BD.length;
            }
    }
    
    public void run()
    {
        try 
        {
            this.socket_serveur = new ServerSocket(this.port, 10);
            //On attend les connexions
            List<Thread> threads = new ArrayList<>();
            int i=0;
            while(this.continuer)
            {
                threads.add(new Thread(new Accepter_client(this.socket_serveur.accept(), this)));
                threads.get(i).start();
                i++;
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
    private Communication_serveur com_serveur;
    
    public Accepter_client(Socket s, Communication_serveur com_serveur)
    {
        System.out.println("Connexion d'un client.");
        this.socket = s;
        this.parametres = new Parametres();
        this.communication = new Communication();
        this.com_serveur = com_serveur;
    }

    public void run() 
    {
        int choix_client;
        try 
        {
            //Mise en place des canaux de communication
            this.dos = new ObjectOutputStream(this.socket.getOutputStream());
            this.dis = new ObjectInputStream(this.socket.getInputStream());
            
            //Réception de la demande du client
            choix_client = this.dis.readInt();
            
            //Exécution de la demande du client
            switch(choix_client)
            {
                //Réception des schémas
                case 0 :
                    System.out.println("Action serveur : Réception des schémas d'un serveur S.");
                    this.reception_schemas(); break;
                //Réception des schémas du programme P
                case 1 : 
                    System.out.println("Action serveur : Réception des schémas du programme P.");
                    this.reception_schemas_programme(); break;
                //Réception requête BD
                case 2 : 
                    System.out.println("Action serveur : Réception d'une requête.");
                    this.executer_requete(); break;
                //Réception de l'initialisation depuis programme P
                case 3 : 
                    System.out.println("Action serveur : Réception de l'initialisation du programme P.");
                    this.reception_initialisation_programme(); break;
                //Réception de l'initialisation d'un serveurs S
                case 4 : 
                    System.out.println("Action serveur : Réception de l'initialisation d'un serveur S.");
                    this.reception_initialisation(); break;
                //Réception de la fin de maj de BD
                case 5 : 
                    System.out.println("Action serveur : Réception d'une fin de MAJ BD.");
                    this.com_serveur.bd_maj_dispo(); break;
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
            int taille = this.dis.readInt();
            String contenu = (String)this.dis.readObject();
            out.write(contenu.substring(0, taille));
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
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
        this.reception_fichier(this.parametres.getChemin_schemas()+"/global_nouveau.json");
        System.out.println("Schéma local reçu.");
        
        //Réception du schéma global
        this.reception_fichier(this.parametres.getChemin_schemas()+"/local.json");
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
        File source = new File(schemas_a_envoyer+"/local_"+num_local+".json");
        File dest = new File(chemin_schemas+"/local.json");
        this.copier_fichier(source, dest);
        
        //Envoi des schémas aux autres serveurs
        this.communication.envoi_schemas(0);   
        
        //Reconstruction de la BD
        this.construction_BD();
        
        //MAJ du schéma global
        source = new File(schemas_a_envoyer+"/global.json");
        dest = new File(chemin_schemas+"/global.json");
        this.copier_fichier(source, dest);
    }
    
    private void reception_initialisation()
    {
        String chemin_schemas = this.parametres.getChemin_schemas();
        //Réception du schéma global
        this.reception_fichier(chemin_schemas+"/global.json");
        System.out.println("Schéma global reçu.");
        
        //Création du schéma local  
        String contenu = "{\n\t\"tables\":\n\t[\n\t]\n}";
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File(chemin_schemas+"/local.json"));
            out.write(contenu);
            out.close();
            System.out.println("Schéma local créé.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void reception_initialisation_programme()
    {
        String schemas_a_envoyer = this.parametres.getSchemas_a_envoyer();
        //Réception du schéma global
        this.reception_fichier(schemas_a_envoyer+"/global.json");
        System.out.println("Schéma global reçu.");
        
        //Récupération des schémas du serveur   
        String chemin_schemas = this.parametres.getChemin_schemas();
        File source = new File(schemas_a_envoyer+"/global.json");
        File dest = new File(chemin_schemas+"/global.json");
        this.copier_fichier(source, dest);
        
        //Création du schéma local  
        String contenu = "{\n\t\"tables\":\n\t[\n\t]\n}";
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File(chemin_schemas+"/local.json"));
            out.write(contenu);
            out.close();
            System.out.println("Schéma local créé.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        //Envoi des schémas aux autres serveurs
        this.communication.envoi_schemas(2);  
    }
    
    private void executer_requete()
    {
        String tables = "";
        String attributs = "";
        String conditions = "";
        
        //Récupération des éléments de la requête
        try 
        {
            System.out.println("Réception des tables.");
            tables = this.dis.readUTF();
            System.out.println("Réception des tables finie : "+tables);
            attributs = this.dis.readUTF();
            System.out.println("Réception des attributs finie : "+attributs);
            conditions = this.dis.readUTF();
            System.out.println("Réception des conditions finie : "+conditions);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Exécution de la requête
        Communication_BD com_BD = new Communication_BD();
        
        //Envoi du résultat
        try 
        {
            this.dos.writeObject((Object)com_BD.requete(tables, attributs, conditions));
            this.dos.flush();
            System.out.println("Requête envoyée.");
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
        
        //Construction des tables qui n'existent pas
        this.construction_tables(bd_actuelle, bd_nouvelle);
        
        //Construction des colonnes qui n'existent pas
        this.construction_colonnes(bd_actuelle, bd_nouvelle);
        
        //Récupération des tuples manquants
        this.recuperation_tuples(bd_nouvelle);
        
        //Attente de la confirmation des autres serveurs
        System.out.println("/***Attente des la confirmations des autres serveurs avant la suppression***/");
        //Envoi de la fin de récupération des tuples aux autres serveurs
        this.communication.envoi_maj_bd();
        while(!this.com_serveur.bd_dispo())
        {
            try {
                sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Accepter_client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Suppression des tuples (pour la fragmentation horizontale ou hybride seulement)
        this.suppression_tuples(bd_nouvelle);
        
        //Suppression des colonnes
        this.suppression_colonnes(bd_actuelle, bd_nouvelle);
        
        //Suppression des tables
        this.suppression_tables(bd_actuelle, bd_nouvelle);
        
        //MAJ du fichier BD_actuelle.json
        File source = new File(this.parametres.getChemin_schemas()+"/local.json");
        File dest = new File(this.parametres.getChemin_schemas()+"/BD_actuelle.json");
        this.copier_fichier(source, dest);
        
        //Réinitialisation de la MAJ BD
        this.com_serveur.initialisation_MAJ_BD();
        
        System.out.println("Mise à jour de la BD terminée !");
    }
    
    private void construction_tables(Schema_local bd_actuelle, Schema_local bd_nouvelle)
    {
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
                new Communication_BD().ajoutTable(tables_nouvelles[i], creation_attributs);
            }
        }
    }
    
    private void construction_colonnes(Schema_local bd_actuelle, Schema_local bd_nouvelle)
    {
        //Construction des colonnes qui n'existent pas
        System.out.println("/***Construction des colonnes***/");
        
        String[] tables_actuelles = bd_actuelle.get_liste_nom_tables();
        String[] tables_nouvelles = bd_nouvelle.get_liste_nom_tables();
        String[] attributs_nouveaux;
        String[] attributs_actuels;
        boolean creation;
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
                        new Communication_BD().ajoutColonne(tables_nouvelles[i], attributs_nouveaux[j], bd_nouvelle.get_type_attribut(tables_nouvelles[i], attributs_nouveaux[j]));
                    }
                }
            }
        }
    }
    
    private void recuperation_tuples(Schema_local bd_nouvelle)
    {
        //Récupération des tuples manquants
        System.out.println("/***Récupération des tuples***/");
        
        String[] tables_nouvelles = bd_nouvelle.get_liste_nom_tables();
        String[] attributs_nouveaux;
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
                tab_conditions = bd_nouvelle.get_attributs_fragment(tables);
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
                    if(tab_conditions!=null)
                    {
                        for(int j=0; j<tab_conditions.length; j++)
                        {
                            if(!conditions.equals(""))
                                conditions += " AND ";
                            conditions += tab_conditions[j][0]+""+tab_conditions[j][1]+""+tab_conditions[j][2];
                        }
                    }
                        
                    //Envoi de la requête aux serveurs
                    for(int j=0; j<liste_serveurs.size(); j++)
                    {
                        System.out.println("Requete au serveur "+liste_serveurs.get(j)+" : Table "+tables+", attributs : "+attributs);
                        System.out.println("Conditions : "+conditions);
                        new Communication_BD().ajoutTuples(this.communication.envoi_requete(liste_serveurs.get(j), tables, attributs, conditions), 
                                tables, bd_nouvelle.get_cles_primaires(tables));
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
                            new Communication_BD().ajoutTuples(this.communication.envoi_requete(num_serveur_envoi_requete, tables, attributs, conditions), 
                                    tables, bd_nouvelle.get_cles_primaires(tables));
                        }
                    }
                }
            }
        }
    }
    
    private void suppression_tuples(Schema_local bd_nouvelle)
    {
        //Suppression des tuples (pour la fragmentation horizontale ou hybride seulement)
        System.out.println("/***Suppression des tuples***/");
        
        String[] tables_nouvelles = bd_nouvelle.get_liste_nom_tables();
        String tables;
        String conditions;
        String[][] tab_conditions;
        for(int i=0; i<tables_nouvelles.length; i++)
        {   
            if(bd_nouvelle.get_table_fragmentation(tables_nouvelles[i]).equals("horizontale") ||
                    bd_nouvelle.get_table_fragmentation(tables_nouvelles[i]).equals("hybride"))
            {
                tab_conditions = null;
                conditions = "";
                //Recherche des tuples souhaités
                tables = tables_nouvelles[i];
                //Définition des conditions
                tab_conditions = bd_nouvelle.get_attributs_fragment(tables);

                for(int j=0; j<tab_conditions.length; j++)
                {
                    if(!conditions.equals(""))
                        conditions += " AND ";
                    conditions += tab_conditions[j][0]+""+this.inverse_signe(tab_conditions[j][1])+""+tab_conditions[j][2];
                    
                }
                System.out.println("Table : "+tables+", suppression : "+conditions);
                new Communication_BD().suppressionTuples(tables, conditions);
            }
        }
    }
    
    private void suppression_colonnes(Schema_local bd_actuelle, Schema_local bd_nouvelle)
    {
        //Suppression des colonnes
        System.out.println("/***Suppression des colonnes***/");
        
        String[] tables_actuelles = bd_actuelle.get_liste_nom_tables();
        String[] tables_nouvelles = bd_nouvelle.get_liste_nom_tables();
        String[] attributs_nouveaux;
        String[] attributs_actuels;
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
                        System.out.println("Suppression de la colonne "+attributs_actuels[j]+" de la table "+tables_nouvelles[i]+".");
                        new Communication_BD().suppressionColonne(tables_nouvelles[i], attributs_actuels[j]);
                    }
                }
            }
        }
    }
    
    private void suppression_tables(Schema_local bd_actuelle, Schema_local bd_nouvelle)
    {
        //Suppression des tables
        System.out.println("/***Suppression des tables***/");
        
        String[] tables_actuelles = bd_actuelle.get_liste_nom_tables();
        String[] tables_nouvelles = bd_nouvelle.get_liste_nom_tables();
        boolean suppression;
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
                new Communication_BD().suppressionTable(tables_nouvelles[i]);
            }
        }
    }
    
    private String inverse_signe(String signe)
    {
        String res = "";
        switch(signe)
        {
            case ">" : res = "<="; break;
            case "<" : res = ">="; break;
            case ">=" : res = "<"; break;
            case "<=" : res = ">"; break;
            case "=" : res = "<>"; break;
            case "<>" : res = "="; break;
            case "LIKE" : res = "NOT LIKE"; break;
            case "NOT LIKE" : res = "LIKE"; break;
        }
        return res;
    }
}
