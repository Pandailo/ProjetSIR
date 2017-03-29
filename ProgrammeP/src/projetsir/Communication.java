/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Annabelle
 */
public class Communication extends Thread {
    private InetAddress ip;
    private int action;
    private int port;
    private Socket socket;
    private ObjectInputStream dis;
    private ObjectOutputStream dos;
    private Parametres parametres;
    
    public Communication(int action)
    {
        this.parametres = new Parametres();
        this.action = action;
        try 
        {
            this.ip = InetAddress.getLocalHost();
        } 
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        }
        this.port = this.parametres.get_port_serveur_local();
    }
    
    public void run()
    {
        try 
        {  
            //Ouverture du socket
            this.socket = new Socket(this.ip, this.port);
            //Mise en place des canaux de communication
            this.dis = new ObjectInputStream(this.socket.getInputStream());
            this.dos = new ObjectOutputStream(this.socket.getOutputStream());

            //Définition du comportement en fonction de l'action
            switch(this.action)
            {
                //Envoi des schémas
                case 0 : this.envoi_schemas(); break;
                //Demande de l'initialisation
                case 1 : this.envoi_initialisation(); break;
            }
            
            //Fermeture du socket
            this.socket.close();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    private void envoi_fichier(String chemin_fichier)
    {
        byte buf[] = new byte[1024];
        InputStream in = null;        
        try
        {
            in = new FileInputStream(chemin_fichier);
            this.dos.flush();
            //Envoi du fichier
            int n;
            String contenu = "";
            int taille = in.available();
            while((n=in.read(buf))!=-1)
                contenu += new String(buf);
            dos.writeUTF(contenu.substring(0, taille-1));
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void envoi_schemas()
    {
        try 
        {
            //Envoi de l'action à effectuer
            this.dos.writeInt(1);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String chemin_schemas = this.parametres.get_chemin_schemas();
        //Envoi du schéma global
        this.envoi_fichier(chemin_schemas+"/global.json");
        System.out.println("Schéma global envoyé.");
        
        //Envoi des schémas locaux
        //Envoi du nombre de schémas locaux
        try {
            this.dos.writeInt(this.parametres.get_nb_serveurs());
        } catch (IOException ex) {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
        int num_serveur = 0;
        for(int i=0; i<this.parametres.get_nb_serveurs(); i++)
        {
            num_serveur = this.parametres.get_num_serveur(i);
            try {
                //Envoi du numéro du serveur associé au schéma local
                this.dos.writeInt(num_serveur);
            } catch (IOException ex) {
                Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.envoi_fichier(chemin_schemas+"/local_"+num_serveur+".json");
            System.out.println("Schéma local du serveur "+num_serveur+" envoyé.");
        }
    }
    
    private void envoi_initialisation()
    {
        try 
        {
            //Envoi de l'action à effectuer
            this.dos.writeInt(3);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Communication.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        String chemin_schemas = this.parametres.get_chemin_schemas();
        //Envoi du schéma global
        this.envoi_fichier(chemin_schemas+"/global.json");
        System.out.println("Schéma global envoyé.");
    }
}
