/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.fragmentation.horizontale;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author yv965015
 */
public class Frag_Horizontale 
{
    int[][] fragmentation;
    private String nom_table;
    private String mintermes;
    
    
    public Frag_Horizontale(int[][] fragmentation, String nom_table, String mintermes)
    {
        this.fragmentation = fragmentation;
        this.nom_table = nom_table;
        this.mintermes = mintermes;
    }
   
    public void construction_fichier()
    {
        String contenu = "";
        contenu = this.construction_fragmentation();
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File("src/fragments/"+this.nom_table));
            out.write(contenu);
            out.close();
            System.out.println("Fichier créé.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private String construction_fragmentation()
    {
        String s = this.mintermes+"\nhorizontale\n";
        for(int i=0; i<fragmentation.length; i++)
        {
            for(int j=0; j<fragmentation[0].length; j++)
            {
                if(fragmentation[i][j]==1)
                    s += "1";
                else
                    s += "0";
                if(j<fragmentation[0].length-1)
                    s += " ";
            }
            s += "\n";
        }
        return s;
    }
}
