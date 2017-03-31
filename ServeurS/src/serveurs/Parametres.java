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
        this.g_json = new Gestion_json(this.chemin_parametres, true);
        this.init_parametres();
    }
    
    //**********Lecture du fichier**********// 
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
    
    public String getIp_serveur_distant_by_num(int num_serveur)
    {
        String ip = "";
        for(int i=0; i<this.getNb_serveurs(); i++)
        {
            if(this.getNum_serveur_distant(i)==num_serveur)
            {
                ip = this.getIp_serveur_distant(i);
                i = this.getNb_serveurs();
            }
        }
        return ip;
    }
    
    public int getPort_serveur_distant(int i)
    {
        return Integer.parseInt(this.serveurs[i][2]);
    }
    
    public int getPort_serveur_distant_by_num(int num_serveur)
    {
        int port = 0;
        for(int i=0; i<this.getNb_serveurs(); i++)
        {
            if(this.getNum_serveur_distant(i)==num_serveur)
            {
                port = this.getPort_serveur_distant(i);
                i = this.getNb_serveurs();
            }
        }
        return port;
    }

    public void setNum_serveur(int num_serveur) 
    {
        this.num_serveur = num_serveur;
    }

    public void setPort(int port) 
    {
        this.port = port;
    }

    public void setChemin_schemas(String chemin_schemas) 
    {
        this.chemin_schemas = chemin_schemas;
    }

    public void setSchemas_a_envoyer(String schemas_a_envoyer) 
    {
        this.schemas_a_envoyer = schemas_a_envoyer;
    }

    public void setServeurs(String[][] serveurs) 
    {
        this.serveurs = serveurs;
    }

    public void setBD_login(String BD_login) 
    {
        this.BD_login = BD_login;
    }

    public void setBD_mdp(String BD_mdp) 
    {
        this.BD_mdp = BD_mdp;
    }
    
    //**********Ecriture du fichier**********//
    public boolean ecriture_parametres()
    {
        //Formation du fichier
        String contenu = "{\n";
        boolean succes;
        contenu += "\t\"num_serveur\": "+this.num_serveur+",\n";
        contenu += "\t\"port\": "+this.port+",\n";
        contenu += "\t\"BD_login\": \""+this.BD_login+"\",\n";
        contenu += "\t\"BD_mdp\": \""+this.BD_mdp+"\",\n";
        contenu += "\t\"stockage_schemas\": \""+this.chemin_schemas+"\",\n";
        contenu += "\t\"schemas_a_envoyer\": \""+this.schemas_a_envoyer+"\",\n";
        contenu += "\t\"serveurs\":\n\t[\n";
        for(int i=0; i<this.getNb_serveurs(); i++)
        {
            contenu += "\t\t{\n";
            contenu += "\t\t\t\"num\": "+this.serveurs[i][0]+",\n";
            contenu += "\t\t\t\"ip\": \""+this.serveurs[i][1]+"\",\n";
            contenu += "\t\t\t\"port\": "+this.serveurs[i][2]+"\n";
            contenu += "\t\t}";
            if(i+1<this.getNb_serveurs())
                contenu += ",";
            contenu += "\n";
        }
        contenu += "\t]";
        contenu += "\n}";
        
        //Ecriture du fichier
        Gestion_json ecriture = new Gestion_json("test.json", false);
        succes = ecriture.ecriture_json(contenu);
        
        //Récupération du nouveau fichier
        this.g_json = new Gestion_json(this.chemin_parametres, true);
        this.init_parametres();
        
        return succes;
    }
}
