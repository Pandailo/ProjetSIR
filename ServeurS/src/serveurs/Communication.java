/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

import java.io.*;

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
    
    public void envoi_schemas()
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
                    Communication_client cc = new Communication_client(ip, port, 0, num_serveur);
                    cc.start();
                }
            else
                System.out.println("Erreur : le dossier des schémas à envoyer n'existe pas. Chemin des paramètres : "+chemin_schemas_a_envoyer+".");
        }
        else
            System.out.println("Erreur dans les paramètres pour le chemin des schémas à envoyer. Valeur : "+chemin_schemas_a_envoyer+".");
    }
}
