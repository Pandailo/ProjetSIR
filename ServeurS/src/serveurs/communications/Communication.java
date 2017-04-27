/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs.communications;

import java.io.*;
import javax.sql.rowset.CachedRowSet;
import serveurs.Parametres;

/**
 *
 * @author Annabelle
 */
public class Communication {
    private Parametres parametres;
    private Communication_serveur cs;
    
    public Communication()
    {
        this.parametres = new Parametres();
        this.cs = null;
    }
    
    public void demarrer_serveur()
    {
        int port = this.parametres.getPort();
        if(port==-1)
            System.out.println("Erreur dans les paramètres, le serveur de communication ne peut pas se lancer. Port : "+port+".");
        else
        {
            this.cs = new Communication_serveur(port);
            this.cs.start();
            System.out.println("Serveur de communication démarré. Port : "+port+".");
        }
    }
    
    public void envoi_schemas(int action)
    {
        int nb_serveurs = this.parametres.getNb_serveurs();
        String chemin_schemas_a_envoyer = this.parametres.getSchemas_a_envoyer();
        if(chemin_schemas_a_envoyer!=null)
        {
            if(new File(chemin_schemas_a_envoyer).exists())
                for(int i=0; i<nb_serveurs; i++)
                {
                    int num_serveur = this.parametres.getNum_serveur_distant(i);
                    String ip = this.parametres.getIp_serveur_distant(i);
                    int port = this.parametres.getPort_serveur_distant(i);
                    Communication_client cc = new Communication_client(ip, port, num_serveur, action);
                    cc.start();
                }
            else
                System.out.println("Erreur : le dossier des schémas à envoyer n'existe pas. Chemin des paramètres : "+chemin_schemas_a_envoyer+".");
        }
        else
            System.out.println("Erreur dans les paramètres pour le chemin des schémas à envoyer. Valeur : "+chemin_schemas_a_envoyer+".");
    }
    
    public void envoi_maj_bd()
    {
        int nb_serveurs = this.parametres.getNb_serveurs();
        for(int i=0; i<nb_serveurs; i++)
        {
            int num_serveur = this.parametres.getNum_serveur_distant(i);
            String ip = this.parametres.getIp_serveur_distant(i);
            int port = this.parametres.getPort_serveur_distant(i);
            Communication_client cc = new Communication_client(ip, port, num_serveur, 3);
            cc.start();
        }
    }
    
    public CachedRowSet envoi_requete(int num_serveur, String tables, String attributs, String conditions)
    {
        CachedRowSet crs = null;
        String ip = this.parametres.getIp_serveur_distant_by_num(num_serveur);
        int port = this.parametres.getPort_serveur_distant_by_num(num_serveur);
        if(!ip.equals("") && port != 0)
        {
            Communication_client cc = new Communication_client(ip, port, tables, attributs, conditions);
            cc.start();
            while(!cc.is_requete_terminee())
            {

            }
            crs = cc.getRes_requete();
        }
        else
            System.out.println("L'ip ou le port du serveur "+num_serveur+" n'est pas correctement défini (vérifier les paramètres).");
        return crs;
    }
}
