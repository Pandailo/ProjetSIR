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
public class ProjetSIR 
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        BEA test=new BEA(4,4,4,3);
        test.remplissage();
        int[][] bea=test.bea();
        for(int i=0;i<bea.length;i++)
        {
            for(int j=0;j<bea[i].length;j++)
            {
                System.out.print(" "+bea[i][j]);
            }
            System.out.println("");
        }
    }
    
}
