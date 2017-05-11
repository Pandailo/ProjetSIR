/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.fragmentation.horizontale;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import projetsir.Parametres;
import projetsir.bd_globale;

/**
 *
 * @author yv965015
 */
public class Frag_Horizontale 
{
    private List<String> fragments;
    private int[][] serveurs;
    private String nom_table;
    
    
    public Frag_Horizontale(ArrayList<String> fragments, int[][] serveurs, String nom_table)
    {
        this.fragments = fragments;
        this.serveurs = serveurs;
        this.nom_table = nom_table;
    }
   
    public void construction_fichier()
    {
        String contenu = "";
        contenu = this.construction_schema_table();
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File("src/fragmentation_temporaire/"+this.nom_table));
            out.write(contenu);
            out.close();
            System.out.println("Fichier créé.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public String construction_schema_table()
    {
        String s ="\t\t{\n\t\t\t\"nom\":\""+this.nom_table+"\",\n";
        s += "\t\t\t\"fragmentation\":\"horizontale\",\n";
        s += "\t\t\t\"attributs\":\n\t\t\t[\n";
        bd_globale bd = new bd_globale();
        String[] pk = bd.get_cles_primaires(this.nom_table);
        String[] att = bd.get_liste_attributs_table(this.nom_table);
        String nom_attribut = "";
        Parametres parametres = new Parametres();
        //Ecriture des attributs
        for(int i=0; i<att.length; i++)
        {
            nom_attribut = att[i];
            s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
            s+= "\t\t\t\t\t\"cle_primaire\":";
            if(bd.is_primary_key(this.nom_table, nom_attribut))
                s += "\"oui\",\n";
            else
                s += "\"non\",\n";
            s += "\t\t\t\t\t\"type\":\""+bd.get_type_attribut(this.nom_table, nom_attribut)+"\"\n";
            s += "\t\t\t\t}";
            if(i<(att.length-1))
                s += ",";
            s += "\n";
        }
        s += "\t\t\t],\n";
        //Ecriture des fragments
        s += "\t\t\t\"fragments\":\n\t\t\t[\n";
        String[] conditions;
        for(int i=0; i<this.fragments.size(); i++)
        {
            s += "\t\t\t\t{\n";
            s += "\t\t\t\t\t\"attributs\":\n\t\t\t\t\t[\n";
            conditions = this.fragments.get(i).split("@");
            for(int j=0; j<conditions.length; j++)
            {
                s += "\t\t\t\t\t\t{\n";
                s += "\t\t\t\t\t\t\t\"attribut\":\""+conditions[j].split(";")[1]+"\",\n";
                s += "\t\t\t\t\t\t\t\"signe\":\""+conditions[j].split(";")[2]+"\",\n";
                s += "\t\t\t\t\t\t\t\"valeur\":\""+conditions[j].split(";")[3]+"\"\n";
                s += "\t\t\t\t\t\t}";
                if(j<conditions.length-1)
                    s += ",";
                s += "\n";
            }
            s += "\t\t\t\t\t],\n";
            s += "\t\t\t\t\t\"serveurs\":\n\t\t\t\t\t[";
            boolean virgule = false;
            for(int j=0; j<this.serveurs[0].length; j++)
            {
                if(this.serveurs[i][j]==1)
                {
                    if(virgule)
                        s += ",";
                    else
                        virgule = true;
                    s += "\n";
                    s += "\t\t\t\t\t\t{\n";
                    s += "\t\t\t\t\t\t\t\"num_serveur\":"+parametres.get_num_serveur(j)+"\n";
                    s += "\t\t\t\t\t\t}";
                }
            }
            s += "\n\t\t\t\t\t]";
            s += "\n\t\t\t\t}";
            if(i<this.fragments.size()-1)
                s += ",";
            s += "\n";
        }
        s += "\t\t\t]\n\t\t}";
        return s;
    }
}
