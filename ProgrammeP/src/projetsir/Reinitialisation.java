/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ag092850
 */
public class Reinitialisation {
    private int num_serveur;
    private Parametres parametres;
    private bd_globale globale;
    
    public Reinitialisation()
    {
        this.parametres = new Parametres();
        this.globale = new bd_globale();
        this.num_serveur = this.parametres.get_num_serveur_local();
        this.construction_schema_global(parametres.get_chemin_schemas());
    }
    
    private void construction_schema_global(String chemin_schemas)
    {
        //List<String> liste_tables = this.getListe_tables();
        String[] liste_tables = this.globale.get_liste_nom_tables();
        String contenu = "{\n\t\"tables\":\n\t[\n";
        for(int i=0; i<liste_tables.length; i++)
        {
            contenu += this.construction_table(liste_tables[i]);
            if(i<liste_tables.length-1)
                contenu += ",";
            contenu += "\n";
        }
        contenu += "\t]\n}";
        
        //Ecriture sur global.json
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File(chemin_schemas+"/global.json"));
            out.write(contenu);
            out.close();
            System.out.println("Schéma global créé.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private String construction_table(String table)
    {
        String s ="\t\t{\n\t\t\t\"nom\":\""+table+"\",\n";
        s += "\t\t\t\"fragmentation\":\"verticale\",\n";
        s += "\t\t\t\"attributs\":\n\t\t\t[\n";
        List<String> cles_primaires = Arrays.asList(this.globale.get_cles_primaires(table));
        String[] attributs = this.globale.get_liste_attributs_table(table);
        int nb_attributs = this.globale.get_nb_attributs(table);
        String nom_attribut = "";
        for(int i=0; i<nb_attributs; i++)
        {
            nom_attribut = attributs[i];
            s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
            s+= "\t\t\t\t\t\"cle_primaire\":";
            if(cles_primaires.contains(nom_attribut))
                s += "\"oui\",\n";
            else
                s += "\"non\",\n";
            s += "\t\t\t\t\t\"type\":\""+this.globale.get_type_attribut(table, nom_attribut)+"\",\n";
            s += "\t\t\t\t\t\"serveurs\":\n\t\t\t\t\t[\n\t\t\t\t\t\t{\n";
            s += "\t\t\t\t\t\t\t\"num_serveur\":"+this.num_serveur+"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n";
            s += "\t\t\t\t}";
            if(i<nb_attributs-1)
                s += ",";
            s += "\n";
        }
        s += "\t\t\t]\n\t\t}";
        return s;
    }
}
