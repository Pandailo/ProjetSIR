
package projetsir.fragmentation.verticale;

public class Frag_verticale 
{
    private String nom_table; //Nom de la tables concernée par la fragmentation
    private String[] nom_attributs; //Nom des attributs de la table
    private int nb_attributs; //Nombre d'attributs
    private int nb_sites; //Nombre de sites
    private int nb_fragments; //Nombre de fragments souhaité
    private int[][] matrice_distribution; //Matrice distribution
    private int[][] matrice_utilisation; //Matrice d'utilisation
    private int[][] fragmentation; //Répartition des attributs dans les fragments
    private BEA bea; 
    private CAM cam;
    
    public Frag_verticale(String nom_table, String[] nom_attributs, int nb_fragments, int[][] matrice_distribution, int[][] matrice_utilisation)
    {
        this.nom_table = nom_table;
        this.nom_attributs = nom_attributs;
        this.nb_fragments = nb_fragments;
        this.matrice_distribution = matrice_distribution;
        this.matrice_utilisation = matrice_utilisation;
        this.nb_sites = this.matrice_distribution[0].length;
        this.nb_attributs = this.matrice_utilisation[0].length;
        
        this.calcul_bea();
        this.calcul_cam();
    }
    
    public int[][] get_fragmentation()
    {
        return this.fragmentation;
    }
    
    private void calcul_bea()
    {
        //Récupération de la matrice BEA
        this.bea = new BEA(this.matrice_utilisation, this.matrice_distribution);
        this.bea.bea();
    }
    
    private void calcul_cam()
    {
        //Récupération de la matrice ordre à partir de BEA + obtention de la fragmentation
        this.cam = new CAM(this.bea.getOrdre_colonnes(), this.matrice_distribution, this.matrice_utilisation, this.nb_fragments);
        this.fragmentation = this.cam.meilleure_fragmentation();
    }
}
