/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.fragmentation.horizontale;

/**
 *
 * @author yv965015
 */
public class Frag_Horizontale 
{
    private int nb_frag;
    private String[][] liste_att;   //[x][y] = x -> numéro de fragments,y -> liste d'attributs concernés , de la forme att;<=;valeur

    public Frag_Horizontale(String[][] liste_att)
    {
        this.liste_att = liste_att;
        nb_frag=liste_att.length;
    }

    public int getNb_frag()
    {
        return nb_frag;
    }

    public void setNb_frag(int nb_frag)
    {
        this.nb_frag = nb_frag;
    }

    public String[][] getListe_att()
    {
        return liste_att;
    }

    public void setListe_att(String[][] liste_att)
    {
        this.liste_att = liste_att;
    }
    
}
