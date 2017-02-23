
package projetsir;

public class Frag_verticale {
    private int taille; //Taille de la matrice
    private int nb_sites; 
    private int[][] mat_d; //Matrice distribution
    private int[][] mat_ut; //Matrice d'utilisation
    private int[][] fragmentation;
    private BEA bea;
    private CAM cam;
    
    public Frag_verticale(int taille, int nb_sites, int[][] mat_d, int[][] mat_ut)
    {
        this.taille = taille;
        this.nb_sites = nb_sites;
        this.mat_d = mat_d;
        this.mat_ut = mat_ut;
    }
    
    public void calcul_bea()
    {
        //Récupération de la matrice BEA
    }
    
    public void calcul_cam()
    {
        //Récupération de la matrice ordre à partir de BEA + obtention de la fragmentation
    }
}
