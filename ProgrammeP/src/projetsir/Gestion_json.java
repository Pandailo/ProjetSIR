/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
import org.json.simple.parser.*;

/**
 *
 * @author Annabelle
 */
public class Gestion_json {
    private String chemin_fichier;
    private JSONObject j_fichier;
    
    public Gestion_json(String chemin_fichier)
    {
        this.chemin_fichier = chemin_fichier;
        this.j_fichier = null;
        JSONParser parser = new JSONParser();
        try 
        {
            this.j_fichier = (JSONObject)parser.parse(new FileReader(this.chemin_fichier));
        } 
        catch (FileNotFoundException ex) 
        {
            Logger.getLogger(Gestion_json.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException | ParseException ex) 
        {
            Logger.getLogger(Gestion_json.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //**********Lecture du fichier**********//    
    public Object get_attribut(String nom_attribut)
    {
        Object o = null;
        //On vérifie que l'attribut existe
        if(this.j_fichier.containsKey(nom_attribut))
            o = this.j_fichier.get(nom_attribut);
        else
            System.out.println("L'attribut "+nom_attribut+" n'existe pas.");
        return o;
    }
    
    public int get_taille_tableau(String nom_tableau)
    {
        JSONArray array = null;
        int taille = -1;
        //On vérifie que le tableau existe
        if(this.j_fichier.containsKey(nom_tableau))
        {
            array = (JSONArray)this.j_fichier.get(nom_tableau);
            taille = array.size();
        }
        else
            System.out.println("Le tableau "+nom_tableau+" n'existe pas.");
        return taille;
    }
    
    public Object get_attribut_tableau(String nom_tableau, int indice_tableau, String nom_attribut)
    {
        JSONArray array = null;
        Object o = null;
        //On vérifie que le tableau existe
        if(this.j_fichier.containsKey(nom_tableau))
        {
            array = (JSONArray)this.j_fichier.get(nom_tableau);
            //On vérifie que l'indice se trouve dans le tableau
            if(array.size()>indice_tableau && indice_tableau>=0)
            {
                JSONObject jo = (JSONObject)array.get(indice_tableau);
                //On vérifie que l'élément du tableau contient l'attribut recherché
                if(jo.containsKey(nom_attribut))
                {
                    o = jo.get(nom_attribut);
                }
                else
                    System.out.println("L'attribut "+nom_attribut+" n'existe pas.");
            }
            else
                System.out.println("Erreur : Nombre d'éléments dans le tableau : "+array.size()+", indice demandé : "+indice_tableau+".");
        }
        else
            System.out.println("Le tableau "+nom_tableau+" n'existe pas.");
        return o;
    }
    
    public int get_taille_tableau_imbrique_niveau_1(String nom_tableau1, int indice_tableau1, String nom_tableau2)
    {
        JSONArray array = (JSONArray)this.get_attribut_tableau(nom_tableau1, indice_tableau1, nom_tableau2);
        int taille = -1;
        //On vérifie que le tableau existe
        if(array!=null)
        {
            taille = array.size();
        }
        else
            System.out.println("Le tableau "+nom_tableau2+" n'existe pas.");
        return taille;
    }
    
    public Object get_attribut_tableau_imbrique_niveau_1(String nom_tableau1, int indice_tableau1, String nom_tableau2,
        int indice_tableau2, String nom_attribut2)
    {
        Object o = null;
        JSONArray array = (JSONArray)this.get_attribut_tableau(nom_tableau1, indice_tableau1, nom_tableau2);
        JSONObject jo = (JSONObject)array.get(indice_tableau2);
        if(jo.containsKey(nom_attribut2))
        {
            o = (Object)jo.get(nom_attribut2);
        }
        else
            System.out.println("L'attribut "+nom_attribut2+" n'existe pas.");
        return o;
    }
    
    public int get_taille_tableau_imbrique_niveau_2(String nom_tableau1, int indice_tableau1, String nom_tableau2,
            int indice_tableau2, String nom_tableau3)
    {
        JSONArray array = (JSONArray)this.get_attribut_tableau_imbrique_niveau_1(nom_tableau1, indice_tableau1, nom_tableau2, 
                indice_tableau2, nom_tableau3);
        int taille = -1;
        //On vérifie que le tableau existe
        if(array!=null)
        {
            taille = array.size();
        }
        else
            System.out.println("Le tableau "+nom_tableau3+" n'existe pas.");
        return taille;
    }
    
    public Object get_attribut_tableau_imbrique_niveau_2(String nom_tableau1, int indice_tableau1, String nom_tableau2,
            int indice_tableau2, String nom_tableau3, int indice_tableau3, String nom_attribut3)
    {
        Object o = null;
        JSONArray array = (JSONArray)this.get_attribut_tableau_imbrique_niveau_1(nom_tableau1, indice_tableau1, 
                nom_tableau2, indice_tableau2, nom_tableau3);
        JSONObject jo = (JSONObject)array.get(indice_tableau3);
        if(jo.containsKey(nom_attribut3))
        {
            o = (Object)jo.get(nom_attribut3);
        }
        else
            System.out.println("L'attribut "+nom_attribut3+" n'existe pas.");
        return o;
    }
}
