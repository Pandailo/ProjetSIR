/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
/**
 *
 * @author Annabelle
 */
public class Communication_client extends Thread {
    private InetAddress ip;
    private int port;
    private Socket socket;
    private ObjectInputStream dis;
    private ObjectOutputStream dos;
    private int action;
    private int serveur;
    private Parametres parametres;
    private String tables;
    private String attributs;
    private String conditions;
    private CachedRowSet res_requete;
    private boolean requete_terminee;
    
    public Communication_client(String ip, int port, int serveur)
    {
        this.action = 0;
        this.serveur = serveur;
        this.parametres = new Parametres();
        try 
        {
            this.ip = InetAddress.getByName(ip);
        } 
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        }
        this.port = port;
    }
    
    public Communication_client(String ip, int port, String tables, String attributs, String conditions)
    {
        this.action = 1;
        this.parametres = new Parametres();
        this.tables = tables;
        this.attributs = attributs;
        this.conditions = conditions;
        this.requete_terminee = false;
        try 
        {
            this.ip = InetAddress.getByName(ip);
        } 
        catch (UnknownHostException e) 
        {
            e.printStackTrace();
        }
        this.port = port;
    }
    
    public boolean is_requete_terminee()
    {
        return this.requete_terminee;
    }
    
    public CachedRowSet getRes_requete()
    {
        return this.res_requete;
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
            
            //Envoi de l'action à effectuer
            this.dos.writeInt(this.action);
            
            //Définition du comportement en fonction de l'action
            switch(this.action)
            {
                //Envoi des schémas
                case 0 : this.envoi_schemas(); break;
                //Demande d'une requête de BD
                case 1 : this.envoi_requete(); break;
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
            this.dos.writeUTF(contenu.substring(0, taille));
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void envoi_schemas()
    {
        //Envoi du schéma global
        this.envoi_fichier(this.parametres.getSchemas_a_envoyer()+"/global.json");
        System.out.println("Scéma global envoyé.");

        //Envoi du schéma local
        this.envoi_fichier(this.parametres.getSchemas_a_envoyer()+"/local_"+this.serveur+".json");
        System.out.println("Scéma local du serveur "+this.serveur+" envoyé.");
    }
    
    private void envoi_requete()
    {
        CachedRowSet crs = null;
        
        try 
        {
            this.dos.writeUTF(this.tables);
            this.dos.writeUTF(this.attributs);
            this.dos.writeUTF(this.conditions);
            
            crs = (CachedRowSet)this.dis.readObject();
        } 
        catch (IOException | ClassNotFoundException ex) 
        {
            Logger.getLogger(Communication_client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.res_requete = crs;
        this.requete_terminee = true;
    }
}
