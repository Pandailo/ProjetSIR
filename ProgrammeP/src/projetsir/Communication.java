/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.io.*;
import static java.lang.Thread.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Annabelle
 */
public class Communication extends Thread {
    private InetAddress ip;
    private int port;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Parametres parametres;
    
    public Communication()
    {
        this.parametres = new Parametres();
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
        String chemin_schemas = this.parametres.get_chemin_schemas();
        //Envoi du schéma global
        this.envoi_fichier(chemin_schemas+"/global.json");
        System.out.println("Scéma global envoyé.");
        
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
            System.out.println("Scéma local du serveur "+num_serveur+" envoyé.");
        }
    }
    
    public void run()
    {
        try 
        {  
            //Ouverture du socket
            this.socket = new Socket(this.ip, this.port);
            //Mise en place des canaux de communication
            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());
            
            //Envoi de l'action à effectuer
            this.dos.writeInt(1);
            
            //Envoi des schémas
            this.envoi_schemas();

            //Fermeture du socket
            this.socket.close();
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}
