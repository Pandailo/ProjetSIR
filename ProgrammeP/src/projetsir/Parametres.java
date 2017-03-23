/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

/**
 *
 * @author Annabelle
 */
public class Parametres {
    private int nb_serveurs;
    private int port_serveur_local;
    private String chemin_schemas;
    private String chemin_parametres;
    private Gestion_json g_json;
    private int[] num_serveurs;
    
    public Parametres()
    {
        this.chemin_parametres = "src/parametres/config.json";
        this.g_json = new Gestion_json(this.chemin_parametres, true);
        this.init_parametres();
    }
    
    //**********Lecture du fichier**********// 
    private void init_parametres()
    {
        //Récupération du nombre du serveurs
        if(this.g_json.get_attribut("nb_serveurs")!=null)
            this.nb_serveurs = (int)(long)this.g_json.get_attribut("nb_serveurs");
        else
            this.nb_serveurs = -1;
        //Récupération du port du serveur local
        if(this.g_json.get_attribut("port_serveur_local")!=null)
            this.port_serveur_local = (int)(long)this.g_json.get_attribut("port_serveur_local");
        else
            this.port_serveur_local = -1;
        //Récupération du dossier de stockage des schémas
        this.chemin_schemas = (String)this.g_json.get_attribut("chemin_schemas");
        //Récupération du numéro des serveurs
        this.num_serveurs = new int[this.nb_serveurs];
        for(int i=0; i<this.g_json.get_taille_tableau("serveurs"); i++)
            this.num_serveurs[i] = (int)(long)this.g_json.get_attribut_tableau("serveurs", i, "num");
    }
    
    public int get_nb_serveurs()
    {
        return this.nb_serveurs;
    }
    
    public int get_port_serveur_local()
    {
        return this.port_serveur_local;
    }
    
    public String get_chemin_schemas()
    {
        return this.chemin_schemas;
    }
    
    public int get_num_serveur(int i)
    {
        return this.num_serveurs[i];
    }
    //**********Ecriture du fichier**********//
    public boolean ecriture_parametres()
    {
        //Formation du fichier
        String contenu = "{\n";
        boolean succes;
        contenu += "\t\"nb_serveurs\": "+this.nb_serveurs+",\n";
        contenu += "\t\"port_serveur_local\": "+this.port_serveur_local+",\n";
        contenu += "\t\"chemin_schemas\": \""+this.chemin_schemas+"\",\n";
        contenu += "\t\"serveurs\":\n\t[\n";
        for(int i=0; i<this.nb_serveurs; i++)
        {
            contenu += "\t\t{\n";
            contenu += "\t\t\t\"num\": "+this.num_serveurs[i]+"\n";
            contenu += "\t\t}";
            if(i+1<this.nb_serveurs)
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
