/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs.arbre_requetes;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author yv965015
 */
public class Arbre
{
    private ArrayList<String> l_att; //un attribut contient la table dans laquelle il est : EMP.empno
    private ArrayList<String> l_con; 
    private Noeud racine;

    public Arbre(ArrayList<String> l_att, ArrayList<String> l_con, String table)
    {
        this.l_att = l_att;
        this.l_con = l_con;
        racine=null;
        if(l_con.size()>0)
        {
            this.construction_jointures();
            this.construction_conditions();
        }
        else
        {
            this.racine = new Noeud(table, null, null, "table");
        }
    }
    
    public void construction_jointures()
    {
        List<String> tables_utilisees = new ArrayList<>();
        boolean jointures_restantes = true;
        while(jointures_restantes)
        {
            jointures_restantes = false;
            for(int i=0;i<l_con.size();i++)
            {
                String[] split=l_con.get(i).split(";");
                String att1,att2,signe;
                att1=split[1];
                signe=split[2];
                att2=split[3];
                if(split[0].equals("J"))
                {
                    if(this.racine==null)
                    {
                        Noeud fg = new Noeud(att1.split("\\.")[0], null, null, "table");
                        Noeud fd = new Noeud(att2.split("\\.")[0], null, null, "table");
                        this.racine = new Noeud(att1+";"+signe+";"+att2, fg, fd, "jointure");
                        tables_utilisees.add(att1.split("\\.")[0]);
                        tables_utilisees.add(att2.split("\\.")[0]);
                        this.l_con.remove(i);
                        i--;
                    }
                    else
                    {
                        if(tables_utilisees.contains(att2.split("\\.")[0]))
                        {
                            Noeud fg = new Noeud(att1.split("\\.")[0], null, null, "table");
                            this.racine = new Noeud(att1+";"+signe+";"+att2, fg, this.racine, "jointure");
                            tables_utilisees.add(att1.split("\\.")[0]);
                            this.l_con.remove(i);
                            i--;
                        }
                        else
                        {
                            if(tables_utilisees.contains(att1.split("\\.")[0]))
                            {
                                Noeud fd = new Noeud(att2.split("\\.")[0], null, null, "table");
                                this.racine = new Noeud(att1+";"+signe+";"+att2, this.racine, fd, "jointure");
                                tables_utilisees.add(att2.split("\\.")[0]);
                                this.l_con.remove(i);
                                i--;
                            }
                            else
                                jointures_restantes = true;
                        }
                    }
                }
            }
        }
    }
    
    public void construction_conditions()
    {
        for(int i=0;i<l_con.size();i++)
        {
            String[] split=l_con.get(i).split(";");
            String att1,att2,signe;
            att1=split[1];
            signe=split[2];
            att2=split[3];
            if(split[0].equals("C"))
            {
                if(racine==null)
                {
                    Noeud fg = new Noeud(att1.split("\\.")[0], null, null, "table");
                    this.racine = new Noeud(att1+";"+signe+";"+att2, fg, null, "condition");
                    this.l_con.remove(i);
                }
                else
                {
                    this.racine = new Noeud(att1+";"+signe+";"+att2, this.racine, null, "condition");
                    this.l_con.remove(i);
                }
            }
        }   
    }
    
    @Override
    public String toString()
    {
        return this.racine.toString();
    }
    
    public List<String> lireArbre()
    {
        List<String> resultat = new ArrayList<>();
        CachedRowSet crs = this.racine.lireNoeud();
        try 
        {
            ResultSetMetaData rsmd = crs.getMetaData();
            List<String> rsmd_colonnes = new ArrayList<>();
            String contenu = "";
            if(this.l_att.size()>0)
            {
                for(int i=0; i<this.l_att.size(); i++)
                {
                    contenu += l_att.get(i);
                    if(i<this.l_att.size()-1)
                        contenu += ";";
                }
            }
            else
            {
                for(int i=0; i<rsmd.getColumnCount(); i++)
                {
                    if(!rsmd_colonnes.contains(rsmd.getColumnName(i+1)))
                    {
                        rsmd_colonnes.add(rsmd.getColumnName(i+1));
                        contenu += rsmd.getColumnName(i+1);
                        if(i<rsmd.getColumnCount()-1)
                            contenu += ";";
                    }
                }
            }
            resultat.add(contenu);
            crs.beforeFirst();
            while(crs.next())
            {
                contenu = "";
                if(this.l_att.size()>0)
                {
                    for(int i=0; i<this.l_att.size(); i++)
                    {
                        contenu += crs.getObject(this.l_att.get(i).split("\\.")[1]).toString();
                        if(i<this.l_att.size()-1)
                            contenu += ";";
                    }
                }
                else
                {
                    for(int i=0; i<rsmd_colonnes.size(); i++)
                    {
                        contenu += crs.getObject(rsmd_colonnes.get(i)).toString();
                        if(i<rsmd.getColumnCount()-1)
                            contenu += ";";
                    }
                }
                resultat.add(contenu);
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Arbre.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resultat;
    }
}
