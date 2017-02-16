/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

/**
 *
 * @author profil
 */
public class BEA 
{
       	private int [][] matD=new int[4][3];//Matrice de distribution
	private int [][] matUt=new int[4][4];//Matrice d'utilisation requetes-> attributs
	private int taille;
        int nbQ;
        int nbA;
        int nbS;
    public int[][] getMatD() 
    {
        return matD;
    }

    public void setNbS(int nbS) {
        this.nbS = nbS;
    }

    public int getNbS() {
        return nbS;
    }

    public BEA(int taille, int nbQ, int nbA, int nbS) {
        this.taille = taille;
        this.nbQ = nbQ;
        this.nbA = nbA;
        this.nbS = nbS;
    }


    public void setNbQ(int nbQ) 
    {
        this.nbQ = nbQ;
    }

    public void setNbA(int nbA)
    {
        this.nbA = nbA;
    }

    public int getNbQ() 
    {
        return nbQ;
    }

    public int getNbA() 
    {
        return nbA;
    }

    public int[][] getMatUt() 
    {
        return matUt;
    }

    public int getTaille() 
    {
            
        return taille;
    }

    public void setMatD(int[][] matD) 
    {
        this.matD = matD;
    }

    public void setMatUt(int[][] matUt) 
    {
        this.matUt = matUt;
    }

    public void setTaille(int taille) 
    {
        this.taille = taille;
    }
   
        
        void remplissage()
	{
		taille=4;
                nbQ=nbA=4;
                nbS=3;
		matD[0][0]=15;
		matD[0][1]=20;
		matD[0][2]=10;
		matD[1][0]=5;
		matD[1][1]=0;
		matD[1][2]=0;
		matD[2][0]=25;
		matD[2][1]=25;
		matD[2][2]=25;
		matD[3][0]=3;
		matD[3][1]=0;
		matD[3][2]=0;

		matUt[0][0]=1;
		matUt[0][1]=0;
		matUt[0][2]=1;
		matUt[0][3]=0;
		matUt[1][0]=0;
		matUt[1][1]=1;
		matUt[1][2]=1;
		matUt[1][3]=0;
		matUt[2][0]=0;
		matUt[2][1]=1;
		matUt[2][2]=0;
		matUt[2][3]=1;
		matUt[3][0]=0;
		matUt[3][1]=0;
		matUt[3][2]=1;
		matUt[3][3]=1;
	}
        int[][] bea()
	{
                //Calcul affinite
		int[][] tab=new int[nbA][nbA];
		for(int i=0;i<this.nbQ;i++)
                {
                    for(int j=0;j<this.nbA;j++)
                    {
                        for(int k=0;k<this.nbA;k++)
                        {
                            if(this.matUt[i][j]==1&&this.matUt[i][k]==1)
                            {
                                for(int u=0;u<nbS;u++)
                                {
                                    tab[j][k]+=matD[i][u];
                                }
                            }
                        }
                    }
                }
                //remplissage des cotés du tableau par des 0
                int[][] tab2=new int[nbA][nbA+2];
                for(int i=0;i<nbA;i++)
                {
                    for(int j=0;j<nbA+2;j++)
                    {
                        if(j==0||j==nbA+1)
                        {
                            tab2[i][j]=0;
                        }
                        else
                        {
                            tab2[i][j]=tab[i][j-1];
                        }
                    }
                }
                //CULCULE DES PLACEMENTS
                int emp=0;
                int max=0;
                int val1=0;
                int val2=0;
                int val3=0;
                int valT=0;
                int[][] tab3=new int[nbA][nbA+2];
                int[][] tableau_BEA=new int[nbA][nbA];
                //init tab3
                for(int i=0;i<tab2.length;i++)
                {
                    for(int j=0;j<tab2[i].length;j++)
                    {
                        if(i==1)
                        {
                            tab3[i][j]=tab2[i][j];
                        }
                    }
                }
                for(int i=2;i<nbA+1;i++)
                {
                    max=0;
                    for(int j=0;j<nbA+1;j++)
                    {
                        val1=0;//Comparaison avec colonne de gauche (bond)
                        val2=0;//Comparaison avec colonne de droite
                        val3=0;//Comparaison entre les deux collones de base
                        valT=0;//Valeur du cont
                        emp=0;//emplacement retenu

                       //max temporaire
                        //Calcul des bonds
                        for(int k=0;k<nbA;k++)
                        {
                            val1+=tab3[k][j]*tab2[k][i];
                            val2+=tab3[k][j+1]*tab2[k][i];
                            val3+=tab3[k][j]*tab3[k][j+1];
                        }
                        val1=val1*2;
                        val2=val2*2;
                        val3=val3*2;
                        valT=val1+val2-val3;
                        System.out.println("valT :"+valT);
                        if(valT>=max)
                        {
                            emp=j;
                            System.out.println("Emplacement :"+emp);
                            max=valT;
                        }
                    }
                    //Assignation de la colonne à sa place dans le tableau BEA final
                    for(int j =0;j<nbA;j++)
                    {
                        
                        System.out.println("Emp :"+emp);      
                        tableau_BEA[emp][j]=tab3[i][j];
                        System.out.print(""+tab3[i][j]);
                    }
                    System.out.println("OK");
                }
		return tab2;
	}
    
}
