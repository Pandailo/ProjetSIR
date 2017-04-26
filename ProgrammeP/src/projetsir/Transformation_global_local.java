/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Annabelle
 */
public class Transformation_global_local
{
    private Parametres parametres;
    private bd_globale bd;
    private int num_serveur;
    private String[] nom_tables;
    
    public Transformation_global_local()
    {
        this.parametres = new Parametres();
        this.bd = new bd_globale();
        String contenu;
        this.nom_tables = this.bd.get_liste_nom_tables();
        for(int i=0; i<this.parametres.get_nb_serveurs(); i++)
        {
            this.num_serveur = this.parametres.get_num_serveur(i);
            contenu = this.creer_local();
            this.ecrire_fichier(contenu);
        }
    }
    
    private void ecrire_fichier(String contenu)
    {
        String chemin_schemas = this.parametres.get_chemin_schemas();
        try
        {
            FileWriter file_writer = new FileWriter(chemin_schemas+"/local_"+this.num_serveur+".json");
            file_writer.write(contenu);
            file_writer.close();
            System.out.println("Fichier local_"+this.num_serveur+" écrit.");
        } 
        catch (IOException ex)
        {
            Logger.getLogger(Transformation_global_local.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String creer_local()
    {
        String contenu = "";
        contenu += "{\n\t\"tables\":\n\t[\n";
        String a_ecrire = "";
        boolean virgule = false;
        for(int i=0; i<this.nom_tables.length; i++)
        {
            if(this.bd.get_fragmentation_table(this.nom_tables[i]).equals("verticale"))
                a_ecrire += this.construction_table_frag_verticale(this.nom_tables[i]);
            else
                a_ecrire += this.construction_table_frag_horizontale(this.nom_tables[i]);
            if(!a_ecrire.equals(""))
            {
                if(virgule)
                    contenu += ",";
                contenu += "\n";
                contenu += a_ecrire;
                a_ecrire = "";
                virgule = true;
            }
        }
        contenu += "\n\t]\n}";
        return contenu;
    }
    
    private String construction_table_frag_verticale(String table)
    {
        String s = "";
        String[] attributs = this.bd.get_liste_attributs_table(table);
        int nb_attributs = this.bd.get_nb_attributs(table);
        String nom_attribut = "";
        int[] serveurs;
        int attributs_concernes = 0;
        boolean ecrire_attribut = false;
        for(int i=0; i<nb_attributs; i++)
        {
            nom_attribut = attributs[i];
            ecrire_attribut = false;
            serveurs = this.bd.get_num_serveurs(table, nom_attribut);
            for(int j=0; j<serveurs.length; j++)
                if(serveurs[j]==this.num_serveur)
                {
                    j = serveurs.length;
                    ecrire_attribut = true;
                    attributs_concernes++;
                }
            
            if(ecrire_attribut)
            {
                if(attributs_concernes==1)
                {
                    s += "\t\t{\n\t\t\t\"nom\":\""+table+"\",\n";
                    s += "\t\t\t\"fragmentation\":\"verticale\",\n";
                    s += "\t\t\t\"attributs\":\n\t\t\t[\n";
                }
                else
                {
                    s += ",";
                    s += "\n";
                }
                nom_attribut = attributs[i];
                s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
                s+= "\t\t\t\t\t\"cle_primaire\":";
                if(this.bd.is_primary_key(table, nom_attribut))
                    s += "\"oui\",\n";
                else
                    s += "\"non\",\n";
                s += "\t\t\t\t\t\"type\":\""+this.bd.get_type_attribut(table, nom_attribut)+"\"\n";
                s += "\t\t\t\t}";
            }
        }
        if(attributs_concernes>0)
            s += "\n\t\t\t]\n\t\t}";
        return s;
    }
    
    private String construction_table_frag_horizontale(String table)
    {
        String s = "";
        String[] attributs = this.bd.get_liste_attributs_table(table);
        int nb_attributs = this.bd.get_nb_attributs(table);
        String nom_attribut = "";
        int nb_fragments = this.bd.get_nb_fragments(table);
        boolean ecrire = false;
        int[] serveurs;
        for(int i=0; i<nb_fragments; i++)
        {
            serveurs = this.bd.get_serveurs_fragment(table, i);
            for(int j=0; j<serveurs.length; j++)
                if(serveurs[j]==this.num_serveur)
                {
                    j = serveurs.length;
                    i = nb_fragments;
                    ecrire = true;
                }
        }
        if(ecrire)
        {
            s += "\t\t{\n\t\t\t\"nom\":\""+table+"\",\n";
            s += "\t\t\t\"fragmentation\":\"horizontale\",\n";
            s += "\t\t\t\"attributs\":\n\t\t\t[\n";
            //Ecriture attributs
            for(int i=0; i<nb_attributs; i++)
            {
                nom_attribut = attributs[i];
                s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
                s+= "\t\t\t\t\t\"cle_primaire\":";
                if(this.bd.is_primary_key(table, nom_attribut))
                    s += "\"oui\",\n";
                else
                    s += "\"non\",\n";
                s += "\t\t\t\t\t\"type\":\""+this.bd.get_type_attribut(table, nom_attribut)+"\"\n";
                s += "\t\t\t\t}";
                if(i<nb_attributs-1)
                    s += ",";
                s += "\n";
            }
            s += "\t\t\t],\n";
            
            //Ecriture fragments
            String[][] fragment;
            int fragments_concernes = 0;
            for(int i=0; i<nb_fragments; i++)
            {
                serveurs = this.bd.get_serveurs_fragment(table, i);
                fragment = this.bd.get_attributs_fragment(table, i);
                ecrire = false;
                for(int j=0; j<serveurs.length; j++)
                    if(serveurs[j]==this.num_serveur)
                    {
                        j = serveurs.length;
                        i = nb_fragments;
                        ecrire = true;
                        fragments_concernes++;
                    }
                if(ecrire)
                {
                    if(fragments_concernes==1)
                    {
                        s += "\t\t\t\"fragments\":\n\t\t\t[\n";
                        s += "\t\t\t\t{";
                    }
                    else
                        s += ",";
                    s += "\n\t\t\t\t\t\"attributs\":";
                    s += "\n\t\t\t\t\t[\n";
                    
                    for(int j=0; j<fragment.length; j++)
                    {
                        s += "\t\t\t\t\t\t{\n";
                        s += "\t\t\t\t\t\t\t\"attribut\":\""+fragment[j][0]+"\",\n";
                        s += "\t\t\t\t\t\t\t\"signe\":\""+fragment[j][1]+"\",\n";
                        s += "\t\t\t\t\t\t\t\"valeur\":\""+fragment[j][2]+"\"\n";
                        s += "\t\t\t\t\t\t}";
                        if(j<fragment.length-1)
                            s += ",";
                        s += "\n";
                    }
                    s += "\t\t\t\t\t]\n\t\t\t\t}";
                }
            }  
            s += "\n\t\t\t]\n\t\t}";
        }
        return s;
    }
}
