/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author yv965015
 */
public class Verif_Frag_Verticale extends javax.swing.JFrame
{
    String table;
    String[] cleP,attS;
    int[][] frag;
    int[][] site_att;
    int nbS, nbA;
    bd_globale bd;
    private Connection connect;
    private Parametres parametres;
    /**
     * Creates new form Verif_Frag_Verticale
     */
    public Verif_Frag_Verticale(String table,int[][] frag)
    {
        initComponents();
        this.nom_table.setText(table);
        this.table=table;
        this.frag=frag;
        //1er fragment,2° attribut, si 1 là
        bd =new bd_globale();
        cleP=bd.get_cles_primaires(table);
        attS=bd.get_attributs_non_primaires(table);
        GridLayout gd=new GridLayout(frag.length+1,frag[0].length+1);
        this.pan_frag.setLayout(gd);
        for(int i=0;i<frag.length+1;i++)
        {
            if(i==0)
            {

                for(int j=0;j<attS.length+cleP.length+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomA=new JLabel();
                        nomA.setText("");
                        pan_frag.add(nomA);
                    }
                    else
                    {
                        if(j<cleP.length+1)
                        {
                            JLabel nomA=new JLabel();
                            nomA.setText(""+cleP[j-1]);
                            pan_frag.add(nomA); 
                        }
                        else
                        {
                            JLabel nomA=new JLabel();
                            nomA.setText(""+attS[j-(cleP.length+1)]);
                            pan_frag.add(nomA);
                        }
                    }
                }
            }
            else
            {
                 for(int j=0;j<attS.length+cleP.length+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomF=new JLabel();
                        nomF.setText("Fragment "+i);
                        pan_frag.add(nomF); 
                    }
                    else
                    {
                        if(j<cleP.length+1)
                        {
                            JCheckBox ch=new JCheckBox();
                            ch.setName("cleP");
                            ch.setEnabled(false);
                            ch.setSelected(true);
                            this.pan_frag.add(ch);
                        }
                        else
                        {
                            JCheckBox ch=new JCheckBox();
                            ch.setName(""+i+"_"+j);
                            if(frag[i-1][j-(cleP.length+1)]!=-1)
                            {
                                ch.setSelected(true);
                            }
                            this.pan_frag.add(ch);
                        }
                    }
                }
            }
        }
        this.nom_table.setText(table);
        //1er fragment,2° attribut, si 1 là
        //bd_globale bd=new bd_globale();
        Parametres param = new Parametres();
        nbS = param.get_nb_serveurs();
        GridLayout gd2=new GridLayout(frag.length+1,nbS+1);
        this.pan_verif.setLayout(gd2);
        for(int i=0;i<frag.length+1;i++)
        {
            if(i==0)
            {

                for(int j=0;j<nbS+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomS=new JLabel();
                        nomS.setText("");
                        pan_verif.add(nomS);
                    }
                    else
                    {
                        JLabel nomS=new JLabel();
                        nomS.setText("Site :"+param.get_num_serveur(j-1));
                        pan_verif.add(nomS);
                    }
                }
            }
            else
            {
                 for(int j=0;j<nbS+1;j++)
                {
                    if(j==0)
                    {
                        JLabel nomF=new JLabel();
                        nomF.setText("Fragment "+i);
                        pan_verif.add(nomF); 
                    }
                    else
                    {
                        JCheckBox ch=new JCheckBox();
                        ch.setName(""+i+"_"+j);
                        this.pan_verif.add(ch);
                    }
                }
            }
        }
        
    }
private void construction_fichier(String chemin_schemas)
    {
        String contenu = "";

            contenu += this.construction_table(table,site_att);

        //Ecriture sur tablefrag.json
        FileWriter out = null;
        try
        {
            out = new FileWriter(new File(chemin_schemas));
            out.write(contenu);
            out.close();
            System.out.println("Fichier créé.");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private String construction_table(String table, int[][] site_att)
    {
        String s ="\t\t{\n\t\t\t\"nom\":\""+table+"\",\n";
        s += "\t\t\t\"fragmentation\":\"verticale\",\n";
        s += "\t\t\t\"attributs\":\n\t\t\t[\n";

        String[] pk = bd.get_cles_primaires(table);
        List<String> cles_primaires =Arrays.asList(pk);
        String[] att = new String[cleP.length+attS.length];
        for(int i=0;i<cleP.length;i++)
        {
            att[i]=cleP[i];
        }
        for(int i=cleP.length;i<cleP.length+attS.length;i++)
        {
            att[i]=attS[i-cleP.length];
        }

        String nom_attribut = "";
        for(int i=1; i<=nbA; i++)
        {
            nom_attribut = att[i-1];
            s += "\t\t\t\t{\n\t\t\t\t\t\"nom_attribut\":\""+nom_attribut+"\",\n";
            s+= "\t\t\t\t\t\"cle_primaire\":";
            if(cles_primaires.contains(nom_attribut))
                s += "\"oui\",\n";
            else
                s += "\"non\",\n";
            s += "\t\t\t\t\t\"type\":\""+bd.get_type_attribut(table, nom_attribut)+"\",\n";
            s += "\t\t\t\t\t\"serveurs\":\n\t\t\t\t\t[";
            int cpt=0;
            for (int si=1;si<=nbS;si++)
            {
                if (site_att[si-1][i-1]==1)
                {
                    if(cpt>0)
                        s+=",";
                    s+="\n\t\t\t\t\t\t{\n";
                    s+="\t\t\t\t\t\t\t\"num_serveur\":"+si;
                    s+="\n\t\t\t\t\t\t}";
                    cpt++;
                }
            }
            s+="\n\t\t\t\t\t]\n";
            //s += "\t\t\t\t\t\t\t\"num_serveur\":"+this.num_serveur+"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n";
            s += "\t\t\t\t}";
            if(i<att.length)
                s += ",";
            s += "\n";
        }    

        s += "\t\t\t]\n\t\t}";
        return s;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pan_principal = new javax.swing.JPanel();
        nom_table = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        pan_frag = new javax.swing.JPanel();
        pan_verif = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        pan_buttons = new javax.swing.JPanel();
        annuler_button = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        valider_button_dis = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(1080, 700));
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        pan_principal.setLayout(new java.awt.BorderLayout());

        nom_table.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pan_principal.add(nom_table, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        javax.swing.GroupLayout pan_fragLayout = new javax.swing.GroupLayout(pan_frag);
        pan_frag.setLayout(pan_fragLayout);
        pan_fragLayout.setHorizontalGroup(
            pan_fragLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 285, Short.MAX_VALUE)
        );
        pan_fragLayout.setVerticalGroup(
            pan_fragLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        jPanel1.add(pan_frag);

        javax.swing.GroupLayout pan_verifLayout = new javax.swing.GroupLayout(pan_verif);
        pan_verif.setLayout(pan_verifLayout);
        pan_verifLayout.setHorizontalGroup(
            pan_verifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 285, Short.MAX_VALUE)
        );
        pan_verifLayout.setVerticalGroup(
            pan_verifLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        jPanel1.add(pan_verif);

        pan_principal.add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        pan_buttons.setLayout(new java.awt.GridLayout(1, 0));

        annuler_button.setText("Annuler");
        annuler_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annuler_buttonActionPerformed(evt);
            }
        });
        pan_buttons.add(annuler_button);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        valider_button_dis.setText("Valider distribution");
        valider_button_dis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valider_button_disActionPerformed(evt);
            }
        });
        jPanel3.add(valider_button_dis);

        pan_buttons.add(jPanel3);

        jPanel2.add(pan_buttons);

        pan_principal.add(jPanel2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(pan_principal);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void annuler_buttonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_annuler_buttonActionPerformed
    {//GEN-HEADEREND:event_annuler_buttonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_annuler_buttonActionPerformed

    private void valider_button_disActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_valider_button_disActionPerformed
    {//GEN-HEADEREND:event_valider_button_disActionPerformed
        int[][] distri=new int[frag.length][nbS];
        int u=0;
        int k=0;
        for(int i=nbS+2;i<(nbS+1)*(frag.length+1);i++)
        {
               if(this.pan_verif.getComponent(i).getClass()==JCheckBox.class)
               {
                   JCheckBox jb=(JCheckBox)this.pan_verif.getComponent(i);
                   if(jb.isSelected())
                        distri[u][k]=1;
                   else
                        distri[u][k]=0;
                   k++;
                   if(k>=nbS)
                   {
                       u++;
                       k=0;
                   }
               }
        }  
        nbA=bd.get_nb_attributs(table);
        int[][] mat_frag=new int[frag.length][nbA];
        String[] att=bd.get_liste_attributs_table(table);
        u=0;
        k=0;
        for(int i=nbA+2;i<(nbA+1)*(frag.length+1);i++)
        {
               if(this.pan_frag.getComponent(i).getClass()==JCheckBox.class)
               {
                   JCheckBox jb=(JCheckBox)this.pan_frag.getComponent(i);
                   if(jb.isSelected())
                        mat_frag[u][k]=1;
                   else
                        mat_frag[u][k]=0;
                   k++;
                   if(k>=nbA)
                   {
                       u++;
                       k=0;
                   }
               }
        }  
       
        site_att = new int[nbS][nbA];
        for (int f=0; f<frag.length;f++)
        {
            for (int s=0; s<nbS;s++)
            {
                if (distri[f][s]==1)
                {
                    for (int a=0; a<nbA;a++)
                    {
                        site_att[s][a]=mat_frag[f][a];
                    }
                }
            }
        }
        
        parametres = new Parametres();
        this.construction_fichier("src/fragmentation_temporaire/"+table+".txt");
        this.setVisible(false);
    }//GEN-LAST:event_valider_button_disActionPerformed

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
        }
        catch (ClassNotFoundException ex)
        {
            java.util.logging.Logger.getLogger(Verif_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex)
        {
            java.util.logging.Logger.getLogger(Verif_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (IllegalAccessException ex)
        {
            java.util.logging.Logger.getLogger(Verif_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        catch (javax.swing.UnsupportedLookAndFeelException ex)
        {
            java.util.logging.Logger.getLogger(Verif_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton annuler_button;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel nom_table;
    private javax.swing.JPanel pan_buttons;
    private javax.swing.JPanel pan_frag;
    private javax.swing.JPanel pan_principal;
    private javax.swing.JPanel pan_verif;
    private javax.swing.JButton valider_button_dis;
    // End of variables declaration//GEN-END:variables
}
