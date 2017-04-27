/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs.arbre_requetes;

import java.util.*;

/**
 *
 * @author yv965015
 */
public class Arbre
{
    private ArrayList<String> l_att; //un attribut contient la table dans laquelle il est : EMP.empno
    private ArrayList<String> l_con; 
    private Noeud racine;

    public Arbre(ArrayList<String> l_att, ArrayList<String> l_con)
    {
        this.l_att = l_att;
        this.l_con = l_con;
        racine=null;
        this.construction_jointures();
        this.construction_conditions();
        this.construction_selections();
        System.out.println(this.toString());
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
                    if(racine==null)
                    {
                        Noeud fg = new Noeud(att1.split("\\.")[0], null, null, "table");
                        Noeud fd = new Noeud(att2.split("\\.")[0], null, null, "table");
                        this.racine = new Noeud(att1+";"+signe+";"+att2, fg, fd, "jointure");
                        tables_utilisees.add(att1.split("\\.")[0]);
                        tables_utilisees.add(att2.split("\\.")[0]);
                        this.l_con.remove(i);
                    }
                    else
                    {
                        if(tables_utilisees.contains(att2.split("\\.")[0]))
                        {
                            Noeud fg = new Noeud(att1.split("\\.")[0], null, null, "table");
                            this.racine = new Noeud(att1+";"+signe+";"+att2, fg, this.racine, "jointure");
                            tables_utilisees.add(att1.split("\\.")[0]);
                            tables_utilisees.add(att2.split("\\.")[0]);
                            this.l_con.remove(i);
                        }
                        else
                            jointures_restantes = true;
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
                    Noeud fg = new Noeud(att1+";"+signe+";"+att2, this.racine, null, "condition");
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
    
    public void construction_selections()
    {
        for(int i=0;i<this.l_att.size();i++)
            this.racine = new Noeud(this.l_att.get(i), this.racine, null, "selection");
    }
    
    @Override
    public String toString()
    {
        return this.racine.toString();
    }
    
}
