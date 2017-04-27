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
                    //cé jw1tur ont rékupaientre
                    if(racine==null)
                    {
                        Noeud fg = new Noeud(att1.split("\\.")[0], null, null);
                        Noeud fd = new Noeud(att2.split("\\.")[0], null, null);
                        this.racine = new Noeud(att1+";"+signe+";"+att2, fg, fd);
                        tables_utilisees.add(att1.split("\\.")[0]);
                        tables_utilisees.add(att2.split("\\.")[0]);
                        this.l_con.remove(i);
                    }
                    else
                    {
                        if(tables_utilisees.contains(att2.split("\\.")[0]))
                        {
                            Noeud fg = new Noeud(att1.split("\\.")[0], null, null);
                            this.racine = new Noeud(att1+";"+signe+";"+att2, fg, this.racine);
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
    
}
