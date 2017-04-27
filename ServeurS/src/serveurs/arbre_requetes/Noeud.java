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
    private boolean feuille;
    private boolean jointure;
    private boolean selection;
    private String contenu;
    private Noeud filsG;
    private Noeud filsD;
    
    public Noeud(String contenu,Noeud filsD,Noeud filsG)
    {
        this.contenu=contenu;
        this.filsD=filsD;
        this.filsG=filsG;
        if(this.filsD==null&&this.filsG==null)
        {
            this.feuille=true;
            this.jointure=this.selection=false;
        }
        else
            if(this.filsD==null||this.filsG==null)
            {
                this.feuille=this.selection=false;
                this.jointure=true;
            }
            else
            {
                this.feuille=this.jointure=false;
                this.selection=true;
            }
    }

    public boolean isFeuille()
    {
        return feuille;
    }

    public void setFeuille(boolean feuille)
    {
        this.feuille = feuille;
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
    
}
