/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs;

import com.sun.rowset.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.sql.rowset.*;

/**
 *
 * @author Annabelle
 */
public class Communication_BD 
{
    private String login;
    private String mdp;
    private String url;
    private Connection connect;
    
    public Communication_BD(String login, String mdp, boolean co_fac)
    {
        this.login = login;
        this.mdp = mdp;
        try 
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            if(co_fac)
                this.url = "jdbc:oracle:thin:@butor:1521:ensb2016";
            else
                this.url = "jdbc:oracle:thin:@ufrsciencestech.u-bourgogne.fr:25561:ensb2016";
            this.connect = DriverManager.getConnection(url, login, mdp);
        } 
        catch (ClassNotFoundException | SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //**********Reconstruction de la BD**********//
    //Ajout d'une colonne dans une table
    public void ajoutColonne(String table, String nom_colonne, String type)
    {
        String requete = "ALTER TABLE "+table+" ADD "+nom_colonne+" "+type;
        try 
        {
            Statement stmt = connect.createStatement();
            stmt.executeQuery(requete);
            System.out.println("Colonne "+nom_colonne+" de type "+type+" a été ajoutée à la table "+table+".");
            stmt.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Suppression d'une colonne dans une table
    public void suppressionColonne(String table, String nom_colonne)
    {
        String requete = "ALTER TABLE "+table+" DROP COLUMN "+nom_colonne;
        try 
        {
            Statement stmt = connect.createStatement();
            stmt.executeQuery(requete);
            System.out.println("Colonne "+nom_colonne+" a été supprimée de la table "+table+".");
            stmt.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Extraction des tuples correspondants à la condition
    public CachedRowSet chargerTable(String table, String attributs, String condition)
    {
        ResultSet res = null;
        CachedRowSet crs = null;
        try 
        {
            crs = new CachedRowSetImpl();
            crs.setCommand("SELECT "+attributs+" FROM "+table+" WHERE "+condition);
            crs.execute(this.connect);
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return crs;
    }
    
    //Ajout des tuples dans la BD
    public void ajoutTuples(CachedRowSet crs, String table, String cle_primaire)
    {
        CachedRowSet crs_local = null;
        try 
        {
            String nom_colonne = "";
            System.out.println("Nom table : "+crs.getTableName());
            crs_local = new CachedRowSetImpl();
            crs_local.setCommand("SELECT * FROM "+table);
            crs_local.execute(this.connect);
            //Récupération de la clé primaire
            List<Object> crs_local_pk = new ArrayList<>();
            crs_local.beforeFirst();
            while(crs_local.next())
                crs_local_pk.add(crs_local.getObject(cle_primaire));
            
            //Insertion/mise à jour des tuples
            crs.beforeFirst();
            int indice = -1;
            while(crs.next())
            {
                if(crs_local_pk.contains(crs.getObject(cle_primaire)))
                    indice = crs_local_pk.indexOf(crs.getObject(cle_primaire));
                else
                    indice = -1;
                if(indice==-1)
                    crs_local.moveToInsertRow();
                else
                    crs_local.absolute(indice+1);
                for(int i=1; i<=crs.getMetaData().getColumnCount(); i++)
                {
                    nom_colonne = crs.getMetaData().getColumnName(i);
                    System.out.println(nom_colonne+" : "+crs.getObject(i));
                    crs_local.updateObject(nom_colonne, crs.getObject(nom_colonne));  
                }
                if(indice==-1)
                {
                    crs_local.insertRow();
                    crs_local.moveToCurrentRow();
                }
                else
                    crs_local.updateRow();
            }
            crs_local.acceptChanges(this.connect);
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Suppression des tuples correspondants à la condition
    public void suppressionTuples(String table, String condition)
    {
        String requete = "DELETE FROM "+table+" WHERE "+condition;
        try 
        {
            Statement stmt = connect.createStatement();
            stmt.executeQuery(requete);
            System.out.println("Des tuples ont été supprimés de la table "+table+".");
            stmt.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //**********Fermeture de la connexion**********// 
    public void closeConnect()
    {
        try 
        {
            this.connect.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
