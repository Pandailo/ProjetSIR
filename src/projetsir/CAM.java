
package sir;

import static java.lang.Math.pow;

public class CAM {
    private int taille; //Taille de la matrice
    private int nb_sites;
    private int[] ordre = new int[taille]; //Ordre des attributs 
    private int[][] BEA = new int[taille][taille];
    private int[][] mat_d = new int[taille][nb_sites]; //Matrice distribution
    private int[][] mat_ut = new int[taille][taille]; //Matrice d'utilisation
    
    public CAM()
    {
        this.taille = 4;
        this.nb_sites = 3;
        this.ordre = new int[taille];
        BEA = new int[taille][taille];
        mat_d = new int[taille][nb_sites];
        mat_ut = new int[taille][taille];
        this.remplissage(); //TODO : lecture de fichiers
    }
    
    private void remplissage()
    {
        ordre[0] = 0;
	ordre[1] = 2;
	ordre[2] = 1;
	ordre[3] = 3;
	
	BEA[0][0] = 45;
	BEA[0][1] = 45;
	BEA[0][2] = 0;
	BEA[0][3] = 0;
	
	BEA[1][0] = 45;
	BEA[1][1] = 53;
	BEA[1][2] = 5;
	BEA[1][3] = 3;
	
	BEA[2][0] = 0;
	BEA[2][1] = 5;
	BEA[2][2] = 80;
	BEA[2][3] = 75;
	
	BEA[3][0] = 0;
	BEA[3][1] = 3;
	BEA[3][2] = 75;
	BEA[3][3] = 78;
	
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
    }
    
    //Savoir si une requête est concernée par les attributs
    private boolean is_concern(int req, int[] attributs)
    {
        boolean reponse = true;
        for(int i=0; i<this.taille; i++)
        {
            if(this.mat_ut[req][i]==1 && attributs[i]==0)
            {
                reponse = false;
                i = this.taille;
            }
        }
        return reponse;
    }
    
    //Valeur d'un placement
    int val_subdivision(int subdivision)
    {
        int[] TQ = new int[this.taille];
        int[] BQ = new int[this.taille];
        int[] OQ = new int[this.taille];

        int[] attributsTQ = new int[this.taille];
        int[] attributsBQ = new int[this.taille];

        int CTQ = 0;
        int CBQ = 0;
        int COQ = 0;

        //Initialisation
        for(int i=0; i<this.taille; i++)
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
        for(int i=0; i<this.taille; i++)
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
        for(int i=0; i<this.taille; i++)
        {
            for(int j=0; j<this.nb_sites; j++)
            {
                CTQ += TQ[i]*this.mat_d[i][j];
                CBQ += BQ[i]*this.mat_d[i][j];
                COQ += OQ[i]*this.mat_d[i][j];
            }
        }

        return (CTQ*CBQ-(int)pow(COQ, 2));
    }
    
    //Donne la meilleure valeur pour la fragmentation
    int[] meilleur_placement()
    {
        int meilleur = 1;
        int val_meilleur = this.val_subdivision(1);
        for(int i=1; i<this.taille; i++)
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
    void permutation()
    {
        int[] mat_resultat = new int[this.taille];
        for(int i=0; i<this.taille; i++)
            mat_resultat[i]  = this.ordre[(i+1)%this.taille];
        this.ordre = mat_resultat;
    }
    
    //Teste toutes les permutations et retourne les fragments séparés de la meilleure manière
    int[][] meilleure_fragmentation()
    {
        int[][] fragmentation = new int[2][this.taille];
        int[] meilleur_placement = this.meilleur_placement();
        for(int i=0; i<this.taille; i++)
        {
            if(i<meilleur_placement[0])
                fragmentation[0][i] = this.ordre[i];
            else
                fragmentation[1][i-meilleur_placement[0]] = this.ordre[i];
        }
        for(int j=0; j<this.taille; j++)
        {
            this.permutation();
            if(this.meilleur_placement()[0]>meilleur_placement[0])
            {
                meilleur_placement = this.meilleur_placement();
                for(int i=0; i<this.taille; i++)
                {
                    if(i<meilleur_placement[0])
                        fragmentation[0][i] = this.ordre[i];
                    else
                        fragmentation[1][i-meilleur_placement[0]] = this.ordre[i];
                }
            }
        }
        System.out.println("p "+meilleur_placement[0]+" v "+meilleur_placement[1]);
        for(int i=0; i<this.taille; i++)
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
        for(int i=0; i<this.taille; i++)
            System.out.println("Ordre "+i+" : "+this.ordre[i]);
        int[][] f = meilleure_fragmentation();
        for(int i=0; i<this.taille; i++)
            System.out.println("Ordre "+i+" : "+this.ordre[i]);
        for(int i=0; i<this.taille; i++)
        {
            System.out.println("F1 "+i+" : "+f[0][i]);
        }
        for(int i=0; i<this.taille; i++)
        {
            System.out.println("F2 "+i+" : "+f[1][i]);
        }
    }
}
