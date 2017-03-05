/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

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
}
