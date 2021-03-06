/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.fragmentation.verticale;


/**
 *
 * @author profil
 */
public class BEA 
{
    private int matrice_utilisation[][];
    private int matrice_distribution[][];
    private int matrice_affinite[][];
    private int nb_attributs;
    private int nb_sites;
    private int nb_requetes;
    private int[] ordre_colonnes;

    public BEA(int[][] matrice_utilisation, int[][] matrice_distribution) 
    {
        this.matrice_utilisation = matrice_utilisation;
        this.matrice_distribution = matrice_distribution;
        nb_attributs=matrice_utilisation[0].length;
        nb_sites=matrice_distribution[0].length;
        nb_requetes=matrice_utilisation.length;
        matrice_affinite=new int[nb_attributs][nb_attributs];
        this.Calcul_affinite();
        ordre_colonnes=new int[nb_attributs];
    }

    public int[] getOrdre_colonnes() 
    {
        return ordre_colonnes;
    }
    
    void Calcul_affinite()
    {
        for(int i=0;i<this.nb_requetes;i++)
        {
            for(int j=0;j<this.nb_attributs;j++)
            {
                for(int k=0;k<this.nb_attributs;k++)
                {
                    if(this.matrice_utilisation[i][j]==1&&this.matrice_utilisation[i][k]==1)
                    {
                        for(int u=0;u<nb_sites;u++)
                        {
                            matrice_affinite[j][k]+=matrice_distribution[i][u];
                        }
                    }
                }

            }
        }
        for(int i=0;i<this.nb_attributs;i++)
        {
            for(int j=0;j<this.nb_attributs;j++)
            {
                System.out.print(matrice_affinite[i][j]+" ");                                
            }
            System.out.println("");
        }
    }
    
    int calcul_bond(int[] vec1,int[] vec2)
    {
        int bond=0;
        for(int  i=0;i<vec1.length;i++)
        {
            bond+=vec1[i]*vec2[i];
        }
        return bond;
    }
    
    int calcul_contribution(int[] gauche,int[] milieu,int[] droite)
    {
        int cont=0;
        cont+=2*calcul_bond(gauche,milieu)+2*calcul_bond(droite,milieu)-2*calcul_bond(gauche,droite);
        return cont;
    }
    
    int[][] bea()
    {
        //Init tableau
        int[][] tableau_intermediaire=new int[this.nb_attributs][this.nb_attributs+2];
        int[][] tableau_final=new int[this.nb_attributs][this.nb_attributs];
        int[][] tableau_placement=new int[this.nb_attributs][this.nb_attributs+2];
        for(int i=0;i<this.nb_attributs;i++)
        {
            ordre_colonnes[i]=-1;
            for(int j=0;j<this.nb_attributs+2;j++)
            {
                if(j<this.nb_attributs)
                {
                    tableau_final[i][j]=0;
                }
                if(j==1||j==2)
                {
                    tableau_intermediaire[i][j]=tableau_placement[i][j]=this.matrice_affinite[i][j-1];
                }
                else
                {
                    tableau_intermediaire[i][j]=tableau_placement[i][j]=0;
                }
            }
        }
        ordre_colonnes[0]=0;ordre_colonnes[1]=1;
        int[] colonneG=new int[this.nb_attributs];
        int[] colonneD=new int[this.nb_attributs];
        //i=colonnes d'attributs traités
        for(int i=2;i<this.nb_attributs;i++)
        {
            int indice_cont=-1;
            int[] colonneT=this.matrice_affinite[i];
            int max_cont=Integer.MIN_VALUE;
            int cont=0;
            for(int j=0;j<i+1;j++)
            {
                for(int k=0; k<this.nb_attributs; k++)
                {
                    colonneG[k] = tableau_intermediaire[k][j];
                    colonneD[k] = tableau_intermediaire[k][j+1];
                }
                cont=calcul_contribution(colonneG,colonneT,colonneD);
                if(cont>max_cont)
                {
                    indice_cont=j;
                    max_cont=cont;
                }
            }

            int indice_placement = 0;
            for(int j=0;j<this.nb_attributs;j++)
            {
                indice_placement = 0;
                for(int k=0; k<this.nb_attributs+2; k++)
                {
                    if(k!=indice_cont+1)
                    {
                        tableau_intermediaire[j][k]=tableau_placement[j][indice_placement];
                        indice_placement++;
                    }
                    else
                    {
                        tableau_intermediaire[j][k]=colonneT[j];
                    }
                }
            }
            for(int j=0;j<this.nb_attributs;j++)
            {
                for(int k=0; k<this.nb_attributs+2; k++)
                {
                    tableau_placement[j][k] = tableau_intermediaire[j][k];
                }
            }
            for(int j=0;j<this.nb_attributs;j++)
            {
                if(ordre_colonnes[j]>=indice_cont)
                {
                    ordre_colonnes[j]++;
                }
            }
            ordre_colonnes[i]=indice_cont;
        }
        for(int i=0;i<this.nb_attributs;i++)
        {
            for(int j=0;j<this.nb_attributs;j++)
            {
                tableau_final[i][j]=tableau_intermediaire[i][j+1];
            }
        }
        int[][] tableau_final2=new int[this.nb_attributs][this.nb_attributs];
        for(int i=0;i<this.nb_attributs;i++)
        {
            tableau_final2[i]=tableau_final[ordre_colonnes[i]];
        }
        return tableau_final2;
    }
}
