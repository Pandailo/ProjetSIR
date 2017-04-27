/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveurs.arbre_requetes;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import serveurs.Parametres;
import serveurs.Schema_global;
import serveurs.Schema_local;
import serveurs.communications.Communication;
import serveurs.communications.Communication_BD;

/**
 *
 * @author yv965015
 */
public class Noeud
{
    private boolean table;
    private boolean jointure;
    private boolean condition;
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
        switch(type)
        {
            case "table" : this.table = true; break;
            case "jointure" : this.jointure = true; break;
            case "condition" : this.jointure = true; break;
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
    
    public CachedRowSet lireNoeud()
    {
        CachedRowSet crs=null;
        Schema_global global=new Schema_global();
        Parametres parametres = new Parametres();
        int num_local = parametres.getNum_serveur();
        Schema_local local=new Schema_local(true);
        if(this.table)
        {
            String table = this.contenu;
            if(global.get_table_fragmentation(table).equals("horizontale"))
            {
                int nb_fragments = global.get_nb_fragments(table);
                for(int i=0; i<nb_fragments; i++)
                {
                    int[] serveurs_fragment = global.get_serveurs_fragment(table, i);
                    boolean fragment_recupere = false;
                    for(int j=0; j<serveurs_fragment.length; j++)
                    {
                        if(serveurs_fragment[j]==num_local)
                        {
                            Communication_BD com_bd = new Communication_BD();
                            crs = this.ajout_tuples(crs, com_bd.requete(table, "*", "1=1"));
                            j = serveurs_fragment.length;
                            fragment_recupere = true;
                        }
                    }
                    if(!fragment_recupere)
                    {
                        Communication communication = new Communication();
                        crs = this.ajout_tuples(crs, communication.envoi_requete(serveurs_fragment[0], table, "*", "1=1"));
                    }
                }
            }
            else
            {
                String[] attributs = global.get_liste_attributs_table(table);
                boolean[] attributs_recuperes = new boolean[attributs.length];
                List<String> attributs_a_recuperer = new ArrayList<>();
                boolean recup_finie = false;
                int nb_serveurs = parametres.getNb_serveurs();
                int serveur_concerne = parametres.getNum_serveur();
                int cpt_serveur = 0;
                while(!recup_finie)
                {
                    attributs_a_recuperer.clear();
                    //Optention de la liste des attributs à récupérer sur le serveur concerné
                    for(int j=0; j<attributs.length; j++)
                    {
                        if(!attributs_recuperes[j])
                        {
                            int[] serveurs_attribut = global.get_num_serveurs(table, attributs[j]);
                            for(int k=0; k<serveurs_attribut.length; k++)
                            {
                                if(serveurs_attribut[k]==serveur_concerne)
                                {
                                    attributs_a_recuperer.add(attributs[j]);
                                    k = serveurs_attribut.length;
                                    attributs_recuperes[j] = true;
                                }
                            }
                        }
                    }
                    //Récupération du CachedRowSet
                    if(attributs_a_recuperer.size()>0)
                    {
                        String requete_attributs = "";
                        for(int k=0; k<attributs_a_recuperer.size(); k++)
                        {
                            requete_attributs += attributs_a_recuperer.get(k);
                            if(k<attributs_a_recuperer.size()-1)
                                requete_attributs += ", ";
                        }
                        if(parametres.getNum_serveur()==serveur_concerne)
                        {
                            Communication_BD com_bd = new Communication_BD();
                            crs = this.ajout_attributs(crs, com_bd.requete(table, requete_attributs, "1=1"));
                        }
                        else
                        {
                            Communication communication = new Communication();
                            crs = this.ajout_attributs(crs, communication.envoi_requete(serveur_concerne, table, requete_attributs, "1=1"));
                        }
                    }
                    //Vérification de la fin de la récupération
                    for(int j=0; j<=attributs_recuperes.length; j++)
                    {
                        if(j==attributs_recuperes.length)
                            recup_finie = true;
                        else
                        {
                            if(!attributs_recuperes[j])
                                j = attributs_recuperes.length+1;
                        }
                    }
                    serveur_concerne = parametres.getNum_serveur_distant(cpt_serveur);
                    cpt_serveur++;
                }
            }
        }
        if(this.jointure)
        {
            String[] split = this.contenu.split(";");
            crs = this.jointure(this.filsD.lireNoeud(), split[1].split("\\.")[1], this.filsG.lireNoeud(), split[3].split("\\.")[1]);
        }
        if(this.condition)
        {
            String[] split = this.contenu.split(";");
            Noeud n = null;
            if(this.filsD!=null)
                n = this.filsD;
            if(this.filsG!=null)
                n = this.filsG;
            crs = this.appliquer_condition(n.lireNoeud(), split[1]+""+split[2]+""+split[3]);
        }
        return crs;
    }
    
    public CachedRowSet appliquer_condition(CachedRowSet crs, String condition)
    {
        CachedRowSet crs_res = null;
        
        return crs_res;
    }
    
    public CachedRowSet ajout_attributs(CachedRowSet crs1, CachedRowSet crs2)
    {
        CachedRowSet crs = null;
        
        return crs;
    }
    
    public CachedRowSet ajout_tuples(CachedRowSet crs1, CachedRowSet crs2)
    {
        CachedRowSet crs = null;
        
        return crs;
    }
    
    public CachedRowSet jointure(CachedRowSet t_gauche, String attribut_gauche, CachedRowSet t_droite, String attribut_droit)
    {
        CachedRowSet crs = null;
        
        return crs;
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
        if(this.filsD!=null)
            s += " Fils droit : "+this.filsD.toString();
        if(this.filsG!=null)
            s += " Fils gauche : "+this.filsG.toString();
        return s;
    }
}
