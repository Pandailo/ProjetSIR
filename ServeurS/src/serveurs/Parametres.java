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
public class Parametres {
    private Gestion_json g_json;
    private String chemin_parametres;
    private int num_serveur;
    private int port;
    private String chemin_schemas;
    private String schemas_a_envoyer;
    private String[][] serveurs;
    private String BD_login;
    private String BD_mdp;
    
    public Parametres()
    {
        this.chemin_parametres = "src/parametres/config.json";
        this.g_json = new Gestion_json(this.chemin_parametres);
        this.g_json.ouverture_json();
        this.init_parametres();
    }
    
    private void init_parametres()
    {
        //Récupération du numéro du serveur
        if(this.g_json.get_attribut("num_serveur")!=null)
            this.num_serveur = (int)(long)this.g_json.get_attribut("num_serveur");
        else
            this.num_serveur = -1;
        //Récupération du port de communication du serveur
        if(this.g_json.get_attribut("port")!=null)
            this.port = (int)(long)this.g_json.get_attribut("port");
        else
            this.port = -1;
        //Récupération des informations de connection à la BD
        this.BD_login = (String)this.g_json.get_attribut("BD_login");
        this.BD_mdp = (String)this.g_json.get_attribut("BD_mdp");
        //Récupération du dossier de stockage des schémas
        this.chemin_schemas = (String)this.g_json.get_attribut("stockage_schemas");
        //Récupération du dossier de stockage des schémas à envoyer
        this.schemas_a_envoyer = (String)this.g_json.get_attribut("schemas_a_envoyer");
        //Récupérations des informations des serveurs distants
        int nb_serveurs = this.g_json.get_taille_tableau("serveurs");
        this.serveurs = new String[nb_serveurs][3];
        for(int i=0; i<nb_serveurs; i++)
        {
            this.serveurs[i][0] = this.g_json.get_attribut_tableau("serveurs", i, "num").toString();
            this.serveurs[i][1] = (String)this.g_json.get_attribut_tableau("serveurs", i, "ip");
            this.serveurs[i][2] = this.g_json.get_attribut_tableau("serveurs", i, "port").toString();
        }
    }

    public int getNum_serveur() 
    {
        return num_serveur;
    }

    public int getPort() 
    {
        return port;
    }

    public String getChemin_schemas() 
    {
        return chemin_schemas;
    }

    public String getSchemas_a_envoyer() 
    {
        return schemas_a_envoyer;
    }

    public String getBD_login() 
    {
        return BD_login;
    }

    public String getBD_mdp() 
    {
        return BD_mdp;
    }
    
    public int getNb_serveurs()
    {
        return this.serveurs.length;
    }
    
    public int getNum_serveur_distant(int i)
    {
        return Integer.parseInt(this.serveurs[i][0]);
    }
    
    public String getIp_serveur_distant(int i)
    {
        return this.serveurs[i][1];
    }
    
    public int getPort_serveur_distant(int i)
    {
        return Integer.parseInt(this.serveurs[i][2]);
    }
}
