/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private int num_serveur;
    
    public Initialisation()
    {
        Parametres parametres = new Parametres();
        this.login = parametres.getBD_login();
        this.mdp = parametres.getBD_mdp();
        this.num_serveur = parametres.get_num_serveur_local();
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
        this.construction_schema_global(parametres.get_chemin_schemas());
        try
        {
            this.connect.close();
        } 
        catch (SQLException ex)
        {
            Logger.getLogger(Initialisation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void construction_schema_global(String chemin_schemas)
    {
        List<String> liste_tables = this.getListe_tables();
        String contenu = "{\n\t\"tables\":\n\t[\n";
        for(int i=0; i<liste_tables.size(); i++)
        {
            contenu += this.construction_table(liste_tables.get(i));
            if(i<liste_tables.size()-1)
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
        try 
        {
            List<String> cles_primaires = this.getPrimary_keys(table);
            Statement stmt = this.connect.createStatement();
            ResultSet res = stmt.executeQuery("SELECT * FROM "+table);
            ResultSetMetaData rsmd = res.getMetaData();
            int nb_attributs = rsmd.getColumnCount();
            String nom_attribut = "";
            for(int i=1; i<=nb_attributs; i++)
            {
                nom_attribut = rsmd.getColumnName(i);
                s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
                s+= "\t\t\t\t\t\"cle_primaire\":";
                if(cles_primaires.contains(nom_attribut))
                    s += "\"oui\",\n";
                else
                    s += "\"non\",\n";
                s += "\t\t\t\t\t\"type\":\""+rsmd.getColumnTypeName(i)+"("+rsmd.getPrecision(i)+")\",\n";
                s += "\t\t\t\t\t\"serveurs\":\n\t\t\t\t\t[\n\t\t\t\t\t\t{\n";
                s += "\t\t\t\t\t\t\t\"num_serveur\":"+this.num_serveur+"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n";
                s += "\t\t\t\t}";
                if(i<nb_attributs)
                    s += ",";
                s += "\n";
            }    
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Initialisation.class.getName()).log(Level.SEVERE, null, ex);
        }
        s += "\t\t\t]\n\t\t}";
        return s;
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
    
    private List<String> getPrimary_keys(String table)
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
