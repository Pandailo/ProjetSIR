

package projetsir;

import static java.lang.Math.*;
import java.util.*;

public class CAM 
{
    private int nb_attributs;
    private int nb_requetes;
    private int nb_sites;
    private int[] ordre; //Ordre des attributs 
    private int[][] matrice_distribution; //Matrice distribution
    private int[][] matrice_utilisation; //Matrice d'utilisation
    private int nb_fragments; //Nombre de fragments demandés
    private int[] meilleure_subdivision;
    private int meilleure_val_subdivision;
    
    public CAM(/*int[] ordre, int[][] matrice_distribution, int[][] matrice_utilisation,*/ int nb_fragments)
    {
        this.remplissage2();
        /*this.ordre = ordre;
        this.matrice_distribution = matrice_distribution;
        this.matrice_utilisation = matrice_utilisation;*/
        this.nb_sites = this.matrice_distribution[0].length;
        this.nb_attributs = this.matrice_utilisation[0].length;
        this.nb_requetes = this.matrice_utilisation.length;
        this.nb_fragments = nb_fragments;
        this.meilleure_subdivision = new int[this.nb_fragments];
        this.meilleure_val_subdivision = Integer.MIN_VALUE;
    }
    
    private void remplissage2()
    {
        ordre = new int[5];
        ordre[0] = 2;
	ordre[1] = 0;
	ordre[2] = 3;
	ordre[3] = 4;
        ordre[4] = 1;
        
        matrice_distribution = new int[2][2];
	matrice_distribution[0][0] = 15;
	matrice_distribution[0][1] = 35;
	
	matrice_distribution[1][0] = 0;
	matrice_distribution[1][1] = 70;
        
        matrice_utilisation = new int[2][5];
	matrice_utilisation[0][0] = 1;
	matrice_utilisation[0][1] = 1;
	matrice_utilisation[0][2] = 1;
	matrice_utilisation[0][3] = 0;
        matrice_utilisation[0][4] = 0;
	
	matrice_utilisation[1][0] = 1;
	matrice_utilisation[1][1] = 0;
	matrice_utilisation[1][2] = 0;
	matrice_utilisation[1][3] = 1;
        matrice_utilisation[1][4] = 1;
    }
    
    private void remplissage()
    {
        ordre = new int[4];
        ordre[0] = 0;
	ordre[1] = 2;
	ordre[2] = 1;
	ordre[3] = 3;
	
        matrice_distribution = new int[4][3];
	matrice_distribution[0][0] = 15;
	matrice_distribution[0][1] = 20;
	matrice_distribution[0][2] = 10;
	
	matrice_distribution[1][0] = 5;
	matrice_distribution[1][1] = 0;
	matrice_distribution[1][2] = 0;
	
	matrice_distribution[2][0] = 25;
	matrice_distribution[2][1] = 25;
	matrice_distribution[2][2] = 25;
	
	matrice_distribution[3][0] = 3;
	matrice_distribution[3][1] = 0;
	matrice_distribution[3][2] = 0;
	
        matrice_utilisation = new int[4][4];
	matrice_utilisation[0][0] = 1;
	matrice_utilisation[0][1] = 0;
	matrice_utilisation[0][2] = 1;
	matrice_utilisation[0][3] = 0;
	
	matrice_utilisation[1][0] = 0;
	matrice_utilisation[1][1] = 1;
	matrice_utilisation[1][2] = 1;
	matrice_utilisation[1][3] = 0;
	
	matrice_utilisation[2][0] = 0;
	matrice_utilisation[2][1] = 1;
	matrice_utilisation[2][2] = 0;
	matrice_utilisation[2][3] = 1;
	
	matrice_utilisation[3][0] = 0;
	matrice_utilisation[3][1] = 0;
	matrice_utilisation[3][2] = 1;
	matrice_utilisation[3][3] = 1;
    }
    
    //Savoir si une requête est concernée par les attributs
    private boolean is_concern(int req, List<Integer> attributs)
    {
        for(int i=0; i<this.nb_attributs; i++)
            if(this.matrice_utilisation[req][i]==1 && !attributs.contains(i))
                return false;
        return true;
    }
    
    //Calcul de z
    private int calcul_z(List<Integer> requetes)
    {
        int z = 0;
        for(int i=0; i<requetes.size(); i++)
            for(int j=0; j<this.nb_sites; j++)
                z += this.matrice_distribution[requetes.get(i)][j];
        return z;
    }
    
    //Valeur d'un placement
    private int val_subdivision(int[] subdivision)
    {
        int val_subdivision = 1;
        List<Integer> requetes_non_utilisees = new ArrayList<Integer>();
        List<Integer> requetes_utilisees = new ArrayList<Integer>();
        List<Integer> attributs_concernes = new ArrayList<Integer>();
        int indice_debut = 0;
        int indice_fin = 0;
        //Initialisation des requêtes non utilisées
        for(int i=0; i<this.nb_requetes; i++)
            requetes_non_utilisees.add(i);
        for(int i=0; i<this.nb_fragments; i++)
        {
            //System.out.println("Placement subdi "+subdivision[i]);
            attributs_concernes.clear();
            requetes_utilisees.clear();
            //Récupération des attributs contenus dans la partie de la subdivision
            if(i==0)
            {
                indice_debut = 0;
                indice_fin = subdivision[i];
            }
            else
            {
                if(i==this.nb_fragments-1)
                {
                    indice_debut = subdivision[i-1];
                    indice_fin = this.nb_attributs;
                }
                else
                {
                    indice_debut = subdivision[i-1];
                    indice_fin = subdivision[i];
                }
            }
            for(int k=indice_debut; k<indice_fin; k++)
                attributs_concernes.add(this.ordre[k]); 
            //Récupération des requêtes concernées par la partie de la subdivision
            for(int k=0; k<this.nb_requetes; k++)
                if(is_concern(k, attributs_concernes))
                {
                    requetes_utilisees.add(k);
                    requetes_non_utilisees.remove((Object)k);
                }
            val_subdivision *= calcul_z(requetes_utilisees);
        }
        val_subdivision -= pow(calcul_z(requetes_non_utilisees), this.nb_fragments);
        return val_subdivision;
    }
    
    //Donne la meilleure valeur pour la fragmentation
    private boolean meilleur_placement()
    {
        int[] meilleur = new int[this.nb_fragments-1];
        int[] subdivision_courante = new int[this.nb_fragments-1];
        for(int i=0; i<this.nb_fragments-1; i++)
        {
            meilleur[i] = i+1;
            subdivision_courante[i] = i+1;
        }
        int val_meilleur = Integer.MIN_VALUE;
        int val_courante = val_meilleur;
        int indice_a_changer = this.nb_fragments-2;
        boolean continuer = true;
        //Déplacement de la fragmentation 
        while(continuer)
        {
            //Vérification de la valeur de la subdivision courante
            val_courante = this.val_subdivision(subdivision_courante);
            if(val_courante>val_meilleur)
            {
                val_meilleur = val_courante;
                for(int i=0; i<this.nb_fragments-1; i++)
                    meilleur[i] = subdivision_courante[i];
            }
            //Changement du placement de la subdivision
            indice_a_changer = this.nb_fragments-2;
            while(indice_a_changer>=0 && 
                    subdivision_courante[indice_a_changer]>=this.nb_attributs-1-(this.nb_fragments-2-indice_a_changer))
                indice_a_changer--;
            if(indice_a_changer==-1)
                continuer = false;
            else
            {
                for(int i=indice_a_changer; i<this.nb_fragments-1; i++)
                {
                    if(i==indice_a_changer)
                        subdivision_courante[i]++;
                    else
                        subdivision_courante[i] = subdivision_courante[i-1]+1;
                }
            }
        }
        //Récupération des résultats dans les variables concernées
        if(val_meilleur>this.meilleure_val_subdivision)
        {
            this.meilleure_val_subdivision = val_meilleur;
            for(int i=0; i<this.nb_fragments-1; i++)
                this.meilleure_subdivision[i] = meilleur[i];
            return true;
        }
        return false;
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
        int[][] fragmentation = new int[this.nb_fragments][this.nb_attributs];
        //Initialisation des fragments
        for(int i=0; i<this.nb_fragments; i++)
            for(int j=0; j<this.nb_attributs; j++)
                fragmentation[i][j] = -1;
        
        int fragment = 0;
        for(int i=0; i<this.nb_attributs; i++)
        {
            if(this.meilleur_placement())
            {
                for(int j=0; j<this.nb_fragments; j++)
                    for(int k=0; k<this.nb_attributs; k++)
                        fragmentation[j][k] = -1;
                fragment = 0;
                for(int j=0; j<this.nb_attributs; j++)
                {
                    if(fragment<this.nb_fragments-1 && j>=this.meilleure_subdivision[fragment])
                        fragment++;
                    fragmentation[fragment][this.ordre[j]] = 1;
                }
            }
            this.permutation();
        }    
        return fragmentation;
    }
    
    void test()
    {
        for(int i=0; i<this.nb_attributs; i++)
            System.out.println("Ordre "+i+" : "+this.ordre[i]);
        int[][] f = meilleure_fragmentation();
        for(int i=0; i<this.nb_fragments; i++)
        {
            for(int j=0; j<this.nb_attributs; j++)
            {
                if(f[i][j]==1)
                    System.out.println("Attribut "+j+", fragment "+i);
            }
        }
    }
}
