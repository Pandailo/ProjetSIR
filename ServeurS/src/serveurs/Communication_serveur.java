/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

import java.io.*;
import static java.lang.Thread.*;
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
    private Parametres parametres;
    /*private String chemin_schemas;
    private String schemas_a_envoyer;*/
    
    public Communication_serveur(int port, Parametres parametres)
    {
        this.port = port;
        this.continuer = true;
        this.parametres = parametres;
    }
    
    public void run()
    {
        try 
        {
            this.socket_serveur = new ServerSocket(this.port);
            //On attend les connexions
            while(this.continuer)
            {
                Thread t = new Thread(new Accepter_client(this.socket_serveur.accept(), this.parametres));
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
    
    public Accepter_client(Socket s, Parametres parametres)
    {
        this.socket = s;
        this.parametres = parametres;
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
        String chemin_schemas = this.parametres.getChemin_schemas();
        //Réception du schéma global
        this.reception_fichier(chemin_schemas+"/global.json");
        System.out.println("Schéma global reçu.");

        //Réception du schéma local
        this.reception_fichier(chemin_schemas+"/local.json");
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
        Communication communication = new Communication();
        communication.envoi_schemas();   
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
}
