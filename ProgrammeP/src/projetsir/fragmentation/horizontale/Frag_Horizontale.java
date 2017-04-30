/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.fragmentation.horizontale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import projetsir.bd_globale;

/**
 *
 * @author yv965015
 */
public class Frag_Horizontale 
{
    private int nb_frag;
    private ArrayList<String> fragments;
    private String nom_table;
    
    
    public Frag_Horizontale(ArrayList<String> fragments, String nom_table)
    {
        this.fragments = fragments;
        this.nb_frag = fragments.size();
        this.nom_table = nom_table;
    }

    public int getNb_frag()
    {
        return nb_frag;
    }

    public void setNb_frag(int nb_frag)
    {
        this.nb_frag = nb_frag;
    }

    public ArrayList<String> getFragments()
    {
        return this.fragments;
    }

    public void setFragments(ArrayList<String> fragments)
    {
        this.fragments = fragments;
    }
    
    public String construction_schema_table(List<String> serveurs)
    {
        String s ="\t\t{\n\t\t\t\"nom\":\""+this.nom_table+"\",\n";
        s += "\t\t\t\"fragmentation\":\"horizontale\",\n";
        s += "\t\t\t\"attributs\":\n\t\t\t[\n";
        bd_globale bd = new bd_globale();
        String[] pk = bd.get_cles_primaires(this.nom_table);
        String[] att = bd.get_liste_attributs_table(this.nom_table);
        String nom_attribut = "";
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
            s += "\t\t\t\t\t\"type\":\""+bd.get_type_attribut(this.nom_table, nom_attribut)+"\",\n";
            s += "\t\t\t\t}";
            if(i<att.length)
                s += ",";
            s += "\n";
        }
        s += "\t\t\t],";
        //Ecriture des fragments
        s += "\t\t\t\"fragments\":\n\t\t\t[\n";
        String[] conditions;
        String[] serveurs_fragment;
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
                else
                    s += "\n";
            }
            s += "\t\t\t\t\t],\n";
            s += "\t\t\t\t\t\"serveurs\":\n\t\t\t\t\t[\n";
            serveurs_fragment = serveurs.get(i).split(";");
            for(int j=0; j<serveurs_fragment.length; j++)
            {
                s += "\t\t\t\t\t\t{\n";
                s += "\t\t\t\t\t\t\t\"num_serveur\":"+serveurs_fragment[j]+"\n";
                s += "\t\t\t\t\t\t}";
                if(j<serveurs.size()-1)
                    s += ",";
                else
                    s += "\n";
            }
        }
        s += "\t\t\t]\n\t\t}";
        return s;
    }
}
