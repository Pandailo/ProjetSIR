/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

import java.io.*;
import static java.lang.Thread.*;
import java.net.*;
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
    
    public Accepter_client(Socket s)
    {
        this.socket = s;
        this.parametres = new Parametres();
    }

    private void reception_fichier(String chemin_fichier)
    {
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File(chemin_fichier));
            //Réception du fichier
            int n;
            String contenu = dis.readUTF();
            out.write(contenu);
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
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
                //Réception requête BD
                case 1 : break;
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
