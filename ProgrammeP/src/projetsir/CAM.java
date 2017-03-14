
package projetsir;

import static java.lang.Math.*;
import java.util.*;

public class CAM 
{
    private int nb_attributs; //Taille de la matrice
    private int nb_sites;
    private int[] ordre; //Ordre des attributs 
    private int[][] mat_d; //Matrice distribution
    private int[][] mat_ut; //Matrice d'utilisation
    private int nb_fragments; //Nombre de fragments demandés
    
    public CAM(int nb_attributs, int nb_sites, int[] ordre, int[][] mat_d, int[][] mat_ut, int nb_fragments)
    {
        this.nb_attributs = nb_attributs;
        this.nb_sites = nb_sites;
        this.ordre = ordre;
        this.mat_d = mat_d;
        this.mat_ut = mat_ut;
        this.nb_fragments = nb_fragments;
        //this.remplissage();
    }
    
    /*private void remplissage()
    {
        ordre[0] = 0;
	ordre[1] = 2;
	ordre[2] = 1;
	ordre[3] = 3;
	
	mat_d[0][0] = 15;
	mat_d[0][1] = 20;
	mat_d[0][2] = 10;
	
	mat_d[1][0] = 5;
	mat_d[1][1] = 0;
	mat_d[1][2] = 0;
	
	mat_d[2][0] = 25;
	mat_d[2][1] = 25;
	mat_d[2][2] = 25;
	
	mat_d[3][0] = 3;
	mat_d[3][1] = 0;
	mat_d[3][2] = 0;
	
	mat_ut[0][0] = 1;
	mat_ut[0][1] = 0;
	mat_ut[0][2] = 1;
	mat_ut[0][3] = 0;
	
	mat_ut[1][0] = 0;
	mat_ut[1][1] = 1;
	mat_ut[1][2] = 1;
	mat_ut[1][3] = 0;
	
	mat_ut[2][0] = 0;
	mat_ut[2][1] = 1;
	mat_ut[2][2] = 0;
	mat_ut[2][3] = 1;
	
	mat_ut[3][0] = 0;
	mat_ut[3][1] = 0;
	mat_ut[3][2] = 1;
	mat_ut[3][3] = 1;
    }*/
    
    //Savoir si une requête est concernée par les attributs
    private boolean is_concern(int req, List<Integer> attributs)
    {
        boolean reponse = true;
        for(int i=0; i<attributs.size(); i++)
        {
            if(this.mat_ut[req][attributs.get(i)]==0)
            {
                reponse = false;
                i = this.nb_attributs;
            }
        }
        return reponse;
    }
    
    //Calcul de z
    private int calcul_z(List<Integer> attributs)
    {
        int z = 0;
        for(int i=0; i<this.nb_attributs; i++)
        {
            if(is_concern(i, attributs))
            {
                for(int j=0; j<this.nb_sites; j++)
                    z += this.mat_d[i][j];
            }
        }
        return z;
    }
    
    //Valeur d'un placement
    private int val_subdivision(int[] subdivision) //A CHANGER
    {
        int val_subdivision = 0;
        List<Integer> requetes_non_utilisees = new ArrayList<Integer>();
        List<Integer> attributs_utilises = new ArrayList<Integer>();
        for(int i=0; i<this.nb_attributs; i++)
            attributs_non_utilises.add(i);
        for(int i=0; i<subdivision.length; i++)
        {
            attributs_utilises.clear();
            for(int j=0; j<subdivision[i]; j++)
            {
                for(int k=0; k<this.nb_attributs; k++)
                if(this.ordre[i])
                attributs_utilises.add();
            }
        }
        return val_subdivision;
        /*int[] TQ = new int[this.nb_attributs];
        int[] BQ = new int[this.nb_attributs];
        int[] OQ = new int[this.nb_attributs];

        int[] attributsTQ = new int[this.nb_attributs];
        int[] attributsBQ = new int[this.nb_attributs];

        int CTQ = 0;
        int CBQ = 0;
        int COQ = 0;

        //Initialisation
        for(int i=0; i<this.nb_attributs; i++)
        {
            TQ[i] = 0;
            BQ[i] = 0;
            OQ[i] = 1;

            if(i<subdivision)
            {
                attributsTQ[this.ordre[i]] = 1;
                attributsBQ[this.ordre[i]] = 0;
            }
            else
            {
                attributsTQ[this.ordre[i]] = 0;
                attributsBQ[this.ordre[i]] = 1;
            }
        }

        //Répartition des requêtes
        for(int i=0; i<this.nb_attributs; i++)
        {
            if(this.is_concern(i, attributsTQ))
            {
                TQ[i] = 1;
                OQ[i] = 0;
            }
            if(this.is_concern(i, attributsBQ))
            {
                BQ[i] = 1;
                OQ[i] = 0;
            }
        }

        //Calcul
        for(int i=0; i<this.nb_attributs; i++)
        {
            for(int j=0; j<this.nb_sites; j++)
            {
                CTQ += TQ[i]*this.mat_d[i][j];
                CBQ += BQ[i]*this.mat_d[i][j];
                COQ += OQ[i]*this.mat_d[i][j];
            }
        }

        return (CTQ*CBQ-(int)pow(COQ, 2));*/
    }
    
    //Donne la meilleure valeur pour la fragmentation
    private int[] meilleur_placement()
    {
        int meilleur = 1;
        int val_meilleur = this.val_subdivision(1);
        for(int i=1; i<this.nb_attributs; i++)
        {
            if(this.val_subdivision(i)>val_meilleur)
            {
                val_meilleur = this.val_subdivision(i);
                meilleur = i;
            }     
        }
        int[] resultat = {meilleur,val_meilleur};
        return resultat;
    }
    
    //Permute la matrice ordre sur la gauche
    private void permutation()
    {
        int[] mat_resultat = new int[this.nb_attributs];
        for(int i=0; i<this.nb_attributs; i++)
            mat_resultat[i]  = this.ordre[(i+1)%this.nb_attributs];
        this.ordre = mat_resultat;
    }
    
    //Teste toutes les permutations et retourne les fragments séparés de la meilleure manière
    public int[][] meilleure_fragmentation()
    {
        int[][] fragmentation = new int[2][this.nb_attributs];
        int[] meilleur_placement = this.meilleur_placement();
        for(int i=0; i<this.nb_attributs; i++)
        {
            if(i<meilleur_placement[0])
                fragmentation[0][i] = this.ordre[i];
            else
                fragmentation[1][i-meilleur_placement[0]] = this.ordre[i];
        }
        for(int j=0; j<this.nb_attributs; j++)
        {
            this.permutation();
            if(this.meilleur_placement()[0]>meilleur_placement[0])
            {
                meilleur_placement = this.meilleur_placement();
                for(int i=0; i<this.nb_attributs; i++)
                {
                    if(i<meilleur_placement[0])
                        fragmentation[0][i] = this.ordre[i];
                    else
                        fragmentation[1][i-meilleur_placement[0]] = this.ordre[i];
                }
            }
        }
        System.out.println("p "+meilleur_placement[0]+" v "+meilleur_placement[1]);
        for(int i=0; i<this.nb_attributs; i++)
        {
            if(i>=meilleur_placement[0])
                fragmentation[0][i] = -1;
            else
                fragmentation[1][i+meilleur_placement[0]-2] = -1;
        }
        return fragmentation;
    }
    
    void test()
    {
        for(int i=0; i<this.nb_attributs; i++)
            System.out.println("Ordre "+i+" : "+this.ordre[i]);
        int[][] f = meilleure_fragmentation();
        for(int i=0; i<this.nb_attributs; i++)
            System.out.println("Ordre "+i+" : "+this.ordre[i]);
        for(int i=0; i<this.nb_attributs; i++)
        {
            System.out.println("F1 "+i+" : "+f[0][i]);
        }
        for(int i=0; i<this.nb_attributs; i++)
        {
            System.out.println("F2 "+i+" : "+f[1][i]);
        }
    }
}
