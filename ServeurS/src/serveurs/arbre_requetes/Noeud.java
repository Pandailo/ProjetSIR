/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs.arbre_requetes;

/**
 *
 * @author yv965015
 */
public class Noeud
{
    private boolean table;
    private boolean jointure;
    private boolean condition;
    private boolean selection;
    private String contenu;
    private Noeud filsG;
    private Noeud filsD;
    
    public Noeud(String contenu,Noeud filsD,Noeud filsG, String type)
    {
        this.contenu=contenu;
        this.filsD=filsD;
        this.filsG=filsG;
        this.table = false;
        this.jointure = false;
        this.condition = false;
        this.selection = false;
        switch(type)
        {
            case "table" : this.table = true; break;
            case "jointure" : this.jointure = true; break;
            case "condition" : this.jointure = true; break;
            case "selection" : this.selection = true; break;
        }
    }

    public boolean isTable()
    {
        return table;
    }

    public void setTable(boolean table)
    {
        this.table = table;
    }

    public boolean isJointure()
    {
        return jointure;
    }

    public void setJointure(boolean jointure)
    {
        this.jointure = jointure;
    }

    public boolean isSelection()
    {
        return selection;
    }

    public void setSelection(boolean selection)
    {
        this.selection = selection;
    }

    public String getContenu()
    {
        return contenu;
    }

    public void setContenu(String contenu)
    {
        this.contenu = contenu;
    }

    public Noeud getFilsG()
    {
        return filsG;
    }

    public void setFilsG(Noeud filsG)
    {
        this.filsG = filsG;
    }

    public Noeud getFilsD()
    {
        return filsD;
    }

    public void setFilsD(Noeud filsD)
    {
        this.filsD = filsD;
    }
    
    @Override
    public String toString()
    {
        String s = this.contenu;
        if(table)
            s += " Type : table ";
        if(jointure)
            s += " Type : jointure ";
        if(condition)
            s += " Type : condition ";
        if(selection)
            s += " Type : selection ";
        if(this.filsD!=null)
            s += " Fils droit : "+this.filsD.toString();
        if(this.filsG!=null)
            s += " Fils gauche : "+this.filsG.toString();
        return s;
    }
}
