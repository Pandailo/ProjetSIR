/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author Annabelle
 */
public class Initialisation {
    private String login;
    private String mdp;
    private String url;
    private Connection connect;
    
    public Initialisation()
    {
        Parametres parametres = new Parametres();
        this.login = parametres.getBD_login();
        this.mdp = parametres.getBD_mdp();
        try 
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.url = "jdbc:oracle:thin:@butor:1521:ensb2016";
            this.connect = DriverManager.getConnection(this.url, this.login, this.mdp);
            System.out.println("Connecté à la BD depuis la fac.");
        } 
        catch (SQLException ex) 
        {
            this.url = "jdbc:oracle:thin:@ufrsciencestech.u-bourgogne.fr:25561:ensb2016";
            try 
            {
                this.connect = DriverManager.getConnection(this.url, this.login, this.mdp);
                System.out.println("Connecté à la BD depuis l'extérieur de la fac.");
            } 
            catch (SQLException ex1) 
            {
                Logger.getLogger(Initialisation.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Initialisation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private List<String> getListe_tables()
    {
        List<String> liste_tables = new ArrayList<>();
        try 
        {
            DatabaseMetaData meta = this.connect.getMetaData();
            ResultSet res = meta.getTables(null, this.login.toUpperCase(), "SIR_%", null);
            while(res.next())
                liste_tables.add(res.getString("TABLE_NAME"));
            res.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Initialisation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return liste_tables;
    }
    
    public List<String> getPrimary_keys(String table)
    {
        List<String> pk = new ArrayList<>();
        try 
        {
            DatabaseMetaData meta = this.connect.getMetaData();
            ResultSet res = meta.getPrimaryKeys(null, this.login.toUpperCase(), table.toUpperCase());
            while(res.next())
                pk.add(res.getString("COLUMN_NAME"));
            res.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Initialisation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pk;
    }
}
