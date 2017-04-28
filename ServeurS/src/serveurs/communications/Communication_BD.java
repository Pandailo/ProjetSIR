/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs.communications;

import com.sun.rowset.*;
import java.sql.*;
import java.util.logging.*;
import javax.sql.rowset.*;
import serveurs.Parametres;

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
    
    public Communication_BD()
    {
        Parametres parametres = new Parametres();
        this.login = parametres.getBD_login();
        this.mdp = parametres.getBD_mdp();
        try 
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.url = "jdbc:oracle:thin:@butor:1521:ensb2016";
            this.connect = DriverManager.getConnection(url, login, mdp);
            System.out.println("Connecté à la BD depuis la fac.");
        } 
        catch (SQLException ex) 
        {
            this.url = "jdbc:oracle:thin:@ufrsciencestech.u-bourgogne.fr:25561:ensb2016";
            try 
            {
                this.connect = DriverManager.getConnection(url, login, mdp);
                System.out.println("Connecté à la BD depuis l'extérieur de la fac.");
            } 
            catch (SQLException ex1) 
            {
                Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //**********Reconstruction de la BD**********//
    //Ajout d'une table
    public void ajoutTable(String table, String attributs)
    {
        String requete = "CREATE TABLE "+table+" ("+attributs+")";
        try 
        {
            Statement stmt = connect.createStatement();
            stmt.executeQuery(requete);
            System.out.println("La table "+table+" a été ajoutée.");
            stmt.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Suppression d'une table
    public void suppressionTable(String table)
    {
        String requete = "DROP TABLE "+table;
        try 
        {
            Statement stmt = connect.createStatement();
            stmt.executeQuery(requete);
            System.out.println("La table "+table+" a été supprimée.");
            stmt.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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
    public CachedRowSet requete(String tables, String attributs, String conditions)
    {
        CachedRowSet crs = null;
        try 
        {
            crs = new CachedRowSetImpl();
            crs.setCommand("SELECT "+attributs+" FROM "+tables+" WHERE "+conditions);
            crs.execute(this.connect);
            crs = crs.createCopy();
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(Communication_BD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return crs;
    }
    
    //Ajout des tuples dans la BD
    public void ajoutTuples(CachedRowSet crs, String table, String[] cles_primaires)
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
            int nb_tuples_local = crs_local.size();
            Object[][] crs_local_pk = new Object[nb_tuples_local][cles_primaires.length];
            crs_local.beforeFirst();
            int i = 0;
            while(crs_local.next())
            {
                for(int j=0; j<cles_primaires.length; j++)
                    crs_local_pk[i][j] = crs_local.getObject(cles_primaires[j]);
                i++;
            }

            //Insertion/mise à jour des tuples
            crs.beforeFirst();
            int indice = -1;
            while(crs.next())
            {
                //On vérifie si le tuple est déjà présent dans la base
                indice = -1;
                for(i=0; i<nb_tuples_local; i++)
                    for(int j=0; j<cles_primaires.length; j++)
                    {
                        if(crs.getObject(cles_primaires[j]).equals(crs_local_pk[i][j]))
                        {
                            if(j>=cles_primaires.length-1)
                            {
                                indice = i;
                                i = nb_tuples_local;  
                                System.out.println("Ce tuple existe déjà.");
                            }
                        }
                        else
                            j = cles_primaires.length;
                    }
                
                if(indice==-1)
                    crs_local.moveToInsertRow();
                else
                    crs_local.absolute(indice+1);
                for(i=1; i<=crs.getMetaData().getColumnCount(); i++)
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
    public void suppressionTuples(String table, String conditions)
    {
        String requete = "DELETE FROM "+table+" WHERE "+conditions;
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
