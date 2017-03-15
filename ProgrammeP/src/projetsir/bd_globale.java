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
        this.chemin_schema_global = p.get_chemin_schemas()+"/bd_globale.json";
        this.g_json = new Gestion_json(this.chemin_schema_global);
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
    
    
}
