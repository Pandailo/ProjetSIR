/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

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
    }
    
    //**********Lecture du fichier**********//
    public void ouverture_json()
    {
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
    
    //**********Ecriture du fichier**********//
}
