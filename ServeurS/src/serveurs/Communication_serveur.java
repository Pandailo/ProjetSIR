/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

import java.io.*;
import java.net.*;
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
    private DataInputStream dis;
    private DataOutputStream dos;
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
            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());
            
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
                case 2 : break;
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
        this.reception_fichier(this.parametres.getChemin_schemas()+"/global.json");
        System.out.println("Schéma global reçu.");

        //Réception du schéma local
        this.reception_fichier(this.parametres.getChemin_schemas()+"/local.json");
        System.out.println("Schéma local reçu.");
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
    
    private void construction_BD()
    {
        Schema_local bd_actuelle = new Schema_local(true);
        Schema_local bd_nouvelle = new Schema_local(false);
        //TODO: automatisation pour savoir si la co se fait à la fac ou non
        Communication_BD com_BD = new Communication_BD(this.parametres.getBD_login(), this.parametres.getBD_mdp(), false);
        
        //Construction des tables qui n'existent pas
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
        
        //Attente de la confirmation des autres serveurs
        //Sotckage des réponses dans un fichier pour y avoir accès sans bloquer le programme ?
        
        //Suppression des tuples
        
        //Suppression des colonnes
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
        /*File source = new File(this.parametres.getChemin_schemas()+"/local.json");
        File dest = new File(this.parametres.getChemin_schemas()+"/BD_actuelle.json");
        this.copier_fichier(source, dest);*/
    }
}
