/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.util.*;

/**
 *
 * @author Annabelle
 */


/**
 *
 * @author yv965015
 */
public class bd_globale {
    private Gestion_json g_json;
    private String chemin_schema_global;
    
    public bd_globale()
    {
        Parametres p = new Parametres();
        this.chemin_schema_global = p.get_chemin_schemas()+"/global.json";
        this.g_json = new Gestion_json(this.chemin_schema_global, true);
    }
    
    //**********Lecture du fichier**********// 
    public int get_nb_tables()
    {
        return this.g_json.get_taille_tableau("tables");
    }
    
    public String[] get_liste_nom_tables()
    {
        String[] tables = new String[this.get_nb_tables()];
        for(int i=0; i<this.get_nb_tables(); i++)
            tables[i] = (String)this.g_json.get_attribut_tableau("tables", i, "nom");
        return tables;
    }
    
    private int get_indice_table(String nom_table)
    {
        for(int i=0; i<this.get_nb_tables(); i++)
            if(((String)this.g_json.get_attribut_tableau("tables", i, "nom")).equals(nom_table))
                return i;
        return -1;
    }
    
    
    public int get_nb_attributs(String nom_table)
    {
        return this.g_json.get_taille_tableau_imbrique_niveau_1("tables", this.get_indice_table(nom_table), "attributs");
    }
    
    public String[] get_liste_attributs_table(String nom_table)
    {
        String[] attributs = null;
        int i = this.get_indice_table(nom_table);
        if(i!=-1)
        {
            int nb_attributs = this.get_nb_attributs(nom_table);
            attributs = new String[nb_attributs];
            for(int j=0; j<nb_attributs; j++)
                attributs[j] = (String)this.g_json.get_attribut_tableau_imbrique_niveau_1("tables", i, "attributs", j, "nom_attribut");
        }
        else
            System.out.println("La table "+nom_table+" n'a pas été trouvée.");
        return attributs;
    }
    
    public String[] get_attributs_non_primaires(String nom_table)
    {
        ArrayList<String> cles_non_primaires = new ArrayList<String>();
        String[] attributs = this.get_liste_attributs_table(nom_table);
        for(int i=0; i<attributs.length; i++)
            if(!this.is_primary_key(nom_table, attributs[i]))
                cles_non_primaires.add(attributs[i]);
        String[] res = new String[cles_non_primaires.size()];
        for(int i=0; i<res.length; i++)
            res[i] = cles_non_primaires.get(i);
        return res;
    }
    
    public String[] get_cles_primaires(String nom_table)
    {
        ArrayList<String> cles_primaires = new ArrayList<String>();
        String[] attributs = this.get_liste_attributs_table(nom_table);
        for(int i=0; i<attributs.length; i++)
            if(this.is_primary_key(nom_table, attributs[i]))
                cles_primaires.add(attributs[i]);
        String[] res = new String[cles_primaires.size()];
        for(int i=0; i<res.length; i++)
            res[i] = cles_primaires.get(i);
        return res;
    }
    
    private int get_indice_attribut(String nom_table, String nom_attribut)
    {
        int i = this.get_indice_table(nom_table);
        for(int j=0; j<this.get_nb_attributs(nom_table); j++)
            if(((String)this.g_json.get_attribut_tableau_imbrique_niveau_1("tables", i, "attributs", j, "nom_attribut")).equals(nom_attribut))
                return j;
        return -1;
    }
    
    public boolean is_primary_key(String nom_table, String nom_attribut)
    {
        int i = this.get_indice_table(nom_table);
        int j = this.get_indice_attribut(nom_table, nom_attribut);
        String pk = null;
        if(i!=-1 && j!=-1)
            pk = (String)this.g_json.get_attribut_tableau_imbrique_niveau_1("tables", i, "attributs", j, "cle_primaire");
        if(pk.equals("oui"))
            return true;
        else
            return false;
    }
    
    public String get_type_attribut(String nom_table, String nom_attribut)
    {
        int i = this.get_indice_table(nom_table);
        int j = this.get_indice_attribut(nom_table, nom_attribut);
        if(i!=-1 && j!=-1)
            return (String)this.g_json.get_attribut_tableau_imbrique_niveau_1("tables", i, "attributs", j, "type");
        else
            return null;
    }
    
    public int[] get_num_serveurs(String nom_table, String nom_attribut)
    {
        int i = this.get_indice_table(nom_table);
        int j = this.get_indice_attribut(nom_table, nom_attribut);
        if(i!=-1 && j!=-1)
        {
            int[] serveurs = null;
            int taille = this.g_json.get_taille_tableau_imbrique_niveau_2("tables", i, "attributs", j, "serveurs");
            if(taille!=-1)
            {
                serveurs = new int[taille];
                for(int k=0; k<serveurs.length; k++)
                    serveurs[k] = (int)(long)this.g_json.get_attribut_tableau_imbrique_niveau_2("tables", i, "attributs", j, "serveurs", k, "num_serveur");
            }
            return serveurs;
        }
        else
            return null;
    }
    
    public int get_nb_fragments(String nom_table)
    {
        int i = this.get_indice_table(nom_table);
        if(i!=-1)
            return this.g_json.get_taille_tableau_imbrique_niveau_1("tables", i, "fragments");
        else
            return -1;
    }
    
    //Dimension 0 : nom de l'attribut
    //Dimension 1 : signe de comparaison
    //Dimension 2 : valeur de comparaison
    public String[][] get_attributs_fragment(String nom_table, int fragment)
    {
        String[][] attributs = null;
        int table = this.get_indice_table(nom_table);
        int nb_attributs = this.g_json.get_taille_tableau_imbrique_niveau_2("tables", table, "fragments", fragment, "attributs");
        if(nb_attributs!=-1)
        {
            attributs = new String[nb_attributs][3];
            for(int i=0; i<nb_attributs; i++)
            {
                attributs[i][0] = (String)this.g_json.get_attribut_tableau_imbrique_niveau_2("tables", table, "fragments", fragment, "attributs", i, "attribut");
                attributs[i][1] = (String)this.g_json.get_attribut_tableau_imbrique_niveau_2("tables", table, "fragments", fragment, "attributs", i, "signe");
                attributs[i][2] = (String)this.g_json.get_attribut_tableau_imbrique_niveau_2("tables", table, "fragments", fragment, "attributs", i, "valeur");
            }
        }
        return attributs;
    }
    
    public int[] get_serveurs_fragment(String nom_table, int fragment)
    {
        int i = this.get_indice_table(nom_table);
        if(i!=-1)
        {
            int[] serveurs = new int[this.g_json.get_taille_tableau_imbrique_niveau_2("tables", i, "fragments", fragment, "serveurs")];
            for(int k=0; k<serveurs.length; k++)
                serveurs[k] = (int)(long)this.g_json.get_attribut_tableau_imbrique_niveau_2("tables", i, "fragments", fragment, "serveurs", k, "num_serveur");
            return serveurs;
        }
        else
            return null;
    }
}
