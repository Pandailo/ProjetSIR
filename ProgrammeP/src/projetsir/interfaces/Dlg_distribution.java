/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.interfaces;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import projetsir.Parametres;
import projetsir.bd_globale;

/**
 *
 * @author Annabelle
 */
public class Dlg_distribution extends javax.swing.JFrame
{
    private String nom_table;
    private int[][] fragmentation;
    private String[] en_tete;
    private int nombre_sites;
    private String type_fragmentation;
    private int[][] distribution;
    /**
     * Creates new form Dlg_distribution
     */
    public Dlg_distribution(String nom_table)
    {
        initComponents();
        this.nom_table = nom_table;
        this.en_tete = null;
        this.recuperation_fragmentation();
        this.initialisation_fragmentation();
        this.initialisation_distribution();
        this.distribution = new int[this.fragmentation.length][this.nombre_sites];
    }

    private void recuperation_fragmentation()
    {
        String filePath = "src/fragments/"+this.nom_table;
        try 
        {
            Scanner scanner = new Scanner(new File(filePath));
            //Récupération du nombre de fragments
            int nb_fragments = -2;
            while(scanner.hasNextLine())
            {
                String ligne = scanner.nextLine();
                if(ligne.length()>0)
                    nb_fragments++;
            }
            scanner.close();
            //Récupération de l'en-tête et du contenu des fragments
            scanner = new Scanner(new File(filePath));
            int cpt = -1;
            while(scanner.hasNextLine()) 
            {
                String ligne = scanner.nextLine();
                if(this.en_tete==null)
                {
                    this.en_tete = ligne.split(" ");
                    this.fragmentation = new int[nb_fragments][this.en_tete.length];
                }
                else
                {
                    if(cpt==-1)
                        this.type_fragmentation = ligne;
                    else
                    {
                        String[] fragment = ligne.split(" ");
                        for(int i=0; i<this.fragmentation[0].length; i++)
                        {
                            if(fragment[i].equals("0"))
                                this.fragmentation[cpt][i] = -1;
                            else
                                this.fragmentation[cpt][i] = 1;
                        }
                    }
                    cpt++;
                }
            }
            scanner.close();
        } 
        catch(FileNotFoundException e) 
        {
            e.printStackTrace();
        }
    }
    
    private void initialisation_fragmentation()
    {
        this.Resume_fragment.setLayout(new GridLayout(this.fragmentation.length+1,this.fragmentation[0].length+1));
        for(int i=0;i<this.fragmentation.length+1;i++)
        {
            if(i==0)
            {
                for(int j=0;j<this.en_tete.length+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomA=new JLabel();
                        nomA.setText("");
                        this.Resume_fragment.add(nomA);
                    }
                    else
                    {
                        JLabel nomA=new JLabel();
                        if(this.type_fragmentation.equals("horizontale"))
                        {
                            String[] split = this.en_tete[j-1].split("@");
                            nomA.setText(""+split[0]+split[1]+split[2]);
                        }
                        else
                            nomA.setText(""+this.en_tete[j-1]);
                        this.Resume_fragment.add(nomA); 
                    }
                }
            }
            else
            {
                 for(int j=0;j<this.fragmentation[0].length+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomF=new JLabel();
                        nomF.setText("Fragment "+i);
                        this.Resume_fragment.add(nomF); 
                    }
                    else
                    {
                        JCheckBox ch=new JCheckBox();
                        ch.setName(""+i+"_"+j);
                        if(this.fragmentation[i-1][j-1]!=-1)
                        {
                            ch.setSelected(true);
                        }
                        ch.setEnabled(false);
                        this.Resume_fragment.add(ch);
                    }
                }
            }
        }
    }
    
    private void initialisation_distribution()
    {
        Parametres param = new Parametres();
        this.nombre_sites = param.get_nb_serveurs();
        this.Distribution.setLayout(new GridLayout(this.fragmentation.length+1, this.nombre_sites+1));
        for(int i=0;i<this.fragmentation.length+1;i++)
        {
            if(i==0)
            {
                for(int j=0;j<this.nombre_sites+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomS=new JLabel();
                        nomS.setText("");
                        this.Distribution.add(nomS);
                    }
                    else
                    {
                        JLabel nomS=new JLabel();
                        nomS.setText("Site :"+param.get_num_serveur(j-1));
                        this.Distribution.add(nomS);
                    }
                }
            }
            else
            {
                 for(int j=0;j<this.nombre_sites+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomF=new JLabel();
                        nomF.setText("Fragment "+i);
                        this.Distribution.add(nomF); 
                    }
                    else
                    {
                        JCheckBox ch=new JCheckBox();
                        ch.setName(""+i+"_"+j);
                        this.Distribution.add(ch);
                    }
                }
            }
        }
    }
    
    private String construction_table_verticale()
    {
        String s ="\t\t{\n\t\t\t\"nom\":\""+this.nom_table+"\",\n";
        s += "\t\t\t\"fragmentation\":\"verticale\",\n";
        s += "\t\t\t\"attributs\":\n\t\t\t[\n";
        bd_globale bd = new bd_globale();
        Parametres parametres = new Parametres();
        String[] attributs = bd.get_liste_attributs_table(this.nom_table);
        String nom_attribut = "";
        List<Integer> serveurs = new ArrayList<>();
        for(int i=0; i<attributs.length; i++)
        {
            nom_attribut = attributs[i];
            s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
            s+= "\t\t\t\t\t\"cle_primaire\":";
            if(bd.is_primary_key(this.nom_table, nom_attribut))
                s += "\"oui\",\n";
            else
                s += "\"non\",\n";
            s += "\t\t\t\t\t\"type\":\""+bd.get_type_attribut(this.nom_table, nom_attribut)+"\",\n";
            s += "\t\t\t\t\t\"serveurs\":\n\t\t\t\t\t[";
            serveurs.clear();
            for(int fr=0; fr<this.fragmentation.length; fr++)
            {
                if(this.fragmentation[fr][i]==1)
                {
                    for(int si=0; si<this.nombre_sites; si++)
                    {
                        if(this.distribution[fr][si]==1 && !serveurs.contains(si))
                        {
                            serveurs.add(si);
                        }
                    }
                }
            }
            for(int si=0; si<serveurs.size(); si++)
            {
                if(si>0)
                    s += ",";
                s += "\n\t\t\t\t\t\t{\n";
                s += "\t\t\t\t\t\t\t\"num_serveur\":"+parametres.get_num_serveur(serveurs.get(si));
                s += "\n\t\t\t\t\t\t}";
            }
            s += "\n\t\t\t\t\t]\n";
            s += "\t\t\t\t}";
            if(i<attributs.length-1)
                s += ",";
            s += "\n";
        }    
        s += "\t\t\t]\n\t\t}";
        return s;
    }
    
    public String construction_table_horizontale()
    {
        String s ="\t\t{\n\t\t\t\"nom\":\""+this.nom_table+"\",\n";
        s += "\t\t\t\"fragmentation\":\"horizontale\",\n";
        s += "\t\t\t\"attributs\":\n\t\t\t[\n";
        bd_globale bd = new bd_globale();
        String[] att = bd.get_liste_attributs_table(this.nom_table);
        String nom_attribut = "";
        Parametres parametres = new Parametres();
        //Ecriture des attributs
        for(int i=0; i<att.length; i++)
        {
            nom_attribut = att[i];
            s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
            s+= "\t\t\t\t\t\"cle_primaire\":";
            if(bd.is_primary_key(this.nom_table, nom_attribut))
                s += "\"oui\",\n";
            else
                s += "\"non\",\n";
            s += "\t\t\t\t\t\"type\":\""+bd.get_type_attribut(this.nom_table, nom_attribut)+"\"\n";
            s += "\t\t\t\t}";
            if(i<(att.length-1))
                s += ",";
            s += "\n";
        }
        s += "\t\t\t],\n";
        //Ecriture des fragments
        s += "\t\t\t\"fragments\":\n\t\t\t[\n";
        List<String> conditions = new ArrayList<>();
        for(int i=0; i<this.fragmentation.length; i++)
        {
            s += "\t\t\t\t{\n";
            s += "\t\t\t\t\t\"attributs\":\n\t\t\t\t\t[\n";
            conditions.clear();
            for(int j=0; j<this.fragmentation[0].length; j++)
            {
                if(this.fragmentation[i][j]==1)
                    conditions.add(this.en_tete[j]);
            }
            for(int j=0; j<conditions.size(); j++)
            {
                s += "\t\t\t\t\t\t{\n";
                s += "\t\t\t\t\t\t\t\"attribut\":\""+conditions.get(j).split("@")[0]+"\",\n";
                s += "\t\t\t\t\t\t\t\"signe\":\""+conditions.get(j).split("@")[1]+"\",\n";
                s += "\t\t\t\t\t\t\t\"valeur\":\""+conditions.get(j).split("@")[2]+"\"\n";
                s += "\t\t\t\t\t\t}";
                if(j<conditions.size()-1)
                    s += ",";
                s += "\n";
            }
            s += "\t\t\t\t\t],\n";
            s += "\t\t\t\t\t\"serveurs\":\n\t\t\t\t\t[";
            boolean virgule = false;
            for(int j=0; j<this.distribution[0].length; j++)
            {
                if(this.distribution[i][j]==1)
                {
                    if(virgule)
                        s += ",";
                    else
                        virgule = true;
                    s += "\n";
                    s += "\t\t\t\t\t\t{\n";
                    s += "\t\t\t\t\t\t\t\"num_serveur\":"+parametres.get_num_serveur(j)+"\n";
                    s += "\t\t\t\t\t\t}";
                }
            }
            s += "\n\t\t\t\t\t]";
            s += "\n\t\t\t\t}";
            if(i<this.fragmentation.length-1)
                s += ",";
            s += "\n";
        }
        s += "\t\t\t]\n\t\t}";
        return s;
    }
    
    public void construction_fichier(String contenu)
    {
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File("src/fragmentation_temporaire/"+this.nom_table));
            out.write(contenu);
            out.close();
            System.out.println("Fichier créé.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel2 = new javax.swing.JPanel();
        Resume_fragment = new javax.swing.JPanel();
        Distribution = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        Annuler = new javax.swing.JButton();
        Valider = new javax.swing.JButton();

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        javax.swing.GroupLayout Resume_fragmentLayout = new javax.swing.GroupLayout(Resume_fragment);
        Resume_fragment.setLayout(Resume_fragmentLayout);
        Resume_fragmentLayout.setHorizontalGroup(
            Resume_fragmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 241, Short.MAX_VALUE)
        );
        Resume_fragmentLayout.setVerticalGroup(
            Resume_fragmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        jPanel2.add(Resume_fragment);

        javax.swing.GroupLayout DistributionLayout = new javax.swing.GroupLayout(Distribution);
        Distribution.setLayout(DistributionLayout);
        DistributionLayout.setHorizontalGroup(
            DistributionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 241, Short.MAX_VALUE)
        );
        DistributionLayout.setVerticalGroup(
            DistributionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        jPanel2.add(Distribution);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        Annuler.setText("Annuler");
        Annuler.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                AnnulerActionPerformed(evt);
            }
        });
        jPanel1.add(Annuler);

        Valider.setText("Valider la distribution");
        Valider.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ValiderActionPerformed(evt);
            }
        });
        jPanel1.add(Valider);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void AnnulerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_AnnulerActionPerformed
    {//GEN-HEADEREND:event_AnnulerActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_AnnulerActionPerformed

    private void ValiderActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ValiderActionPerformed
    {//GEN-HEADEREND:event_ValiderActionPerformed
        int u=0;
        int k=0;
        //Récupération de la distribution
        for(int i=this.nombre_sites+2;i<(this.nombre_sites+1)*(this.fragmentation.length+1);i++)
        {
            if(this.Distribution.getComponent(i).getClass()==JCheckBox.class)
            {
                JCheckBox jb=(JCheckBox)this.Distribution.getComponent(i);
                if(jb.isSelected())
                    this.distribution[u][k]=1;
                else
                    this.distribution[u][k]=0;
                k++;
                if(k>=this.nombre_sites)
                {
                    u++;
                    k=0;
                }
            }
        }
        //Vérification de la bonne distribution
        boolean flag = true;
        boolean valide = true;
        for (int f=0; f<this.distribution.length;f++)
        {
            flag=false;
            for (int s=0; s<this.distribution[0].length;s++)
            {
                if(this.distribution[f][s]==1)
                    flag = true;
            }
            if(!flag)
                valide = false;
        }
        if(valide)
        {
            if(this.type_fragmentation.equals("verticale"))
                this.construction_fichier(this.construction_table_verticale());
            else
                this.construction_fichier(this.construction_table_horizontale());
            this.setVisible(false);
        }
        else
            JOptionPane.showMessageDialog(this, "Chaque fragment doit être présent au minimum sur un site.", "Erreur", javax.swing.JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_ValiderActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try
        {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(Dlg_distribution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(Dlg_distribution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(Dlg_distribution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(Dlg_distribution.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Annuler;
    private javax.swing.JPanel Distribution;
    private javax.swing.JPanel Resume_fragment;
    private javax.swing.JButton Valider;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
