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
    private String fichier_parametres;
    private Gestion_json g_json;
    private Communication_serveur cs;
    
    public Communication(String fichier_parametres)
    {
        this.fichier_parametres = fichier_parametres;
        this.g_json = new Gestion_json(this.fichier_parametres);
        this.g_json.ouverture_json();
        this.cs = null;
    }
    
    public void demarrer_serveur()
    {
        int port = -1;
        if(this.g_json.get_attribut("port")!=null)
            port = (int)(long)this.g_json.get_attribut("port");
        String chemin_schemas = (String)this.g_json.get_attribut("stockage_schemas");
        if(port==-1 || chemin_schemas==null)
            System.out.println("Erreur dans les paramètres, le serveur de communication ne peut pas se lancer. Port : "+port+", "
                    + "chemin de stockage des schémas : "+chemin_schemas+".");
        else
        {
            this.cs = new Communication_serveur(port, chemin_schemas);
            this.cs.start();
            System.out.println("Serveur de communication démarré. Port : "+port+", "
                    + "chemin de stockage des schémas : "+chemin_schemas+".");
        }
    }
    
    public void envoi_schemas()
    {
        int nb_serveurs = this.g_json.get_taille_tableau("serveurs");
        String chemin_schemas = (String)this.g_json.get_attribut("schemas_a_envoyer");
        if(chemin_schemas!=null)
        {
            if(new File(chemin_schemas).exists())
                for(int i=0; i<nb_serveurs; i++)
                {
                    int num_serveur = (int)(long)this.g_json.get_attribut_tableau("serveurs", i, "num");
                    String ip = (String)this.g_json.get_attribut_tableau("serveurs", i, "ip");
                    int port = (int)(long)this.g_json.get_attribut_tableau("serveurs", i, "port");
                    Communication_client cc = new Communication_client(ip, port, 0, num_serveur, chemin_schemas);
                    cc.start();
                }
            else
                System.out.println("Erreur : le dossier des schémas à envoyer n'existe pas. Chemin des paramètres : "+chemin_schemas+".");
        }
        else
            System.out.println("Erreur dans les paramètres pour le chemin des schémas à envoyer. Valeur : "+chemin_schemas+".");
    }
}
