/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.awt.GridLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author yv965015
 */
public class Verif_Frag_Verticale extends javax.swing.JFrame
{

    /**
     * Creates new form Verif_Frag_Verticale
     */
    public Verif_Frag_Verticale(String table,int[][] frag)
    {
        initComponents();
        this.nom_table.setText(table);
        //1er fragment,2° attribut, si 1 là
        bd_globale bd=new bd_globale();
        String[] cleP=bd.get_cles_primaires(table);
        String[] attS=bd.get_attributs_non_primaires(table);
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
                            nomA.setText("Attribut :"+cleP[j-1]);
                            pan_frag.add(nomA); 
                        }
                        else
                        {
                            JLabel nomA=new JLabel();
                            nomA.setText("Attribut :"+attS[j-(cleP.length+1)]);
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
                        JLabel nomA=new JLabel();
                        nomA.setText("Fragment "+i);
                        pan_frag.add(nomA); 
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

        pan_principal = new javax.swing.JPanel();
        nom_table = new javax.swing.JLabel();
        pan_buttons = new javax.swing.JPanel();
        annuler_button = new javax.swing.JButton();
        valider_button = new javax.swing.JButton();
        pan_frag = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1080, 700));
        getContentPane().setLayout(new java.awt.GridLayout());

        pan_principal.setLayout(new java.awt.BorderLayout());

        nom_table.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pan_principal.add(nom_table, java.awt.BorderLayout.NORTH);

        pan_buttons.setLayout(new java.awt.GridLayout());

        annuler_button.setText("Annuler");
        annuler_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                annuler_buttonActionPerformed(evt);
            }
        });
        pan_buttons.add(annuler_button);

        valider_button.setText("Valider");
        pan_buttons.add(valider_button);

        pan_principal.add(pan_buttons, java.awt.BorderLayout.SOUTH);

        javax.swing.GroupLayout pan_fragLayout = new javax.swing.GroupLayout(pan_frag);
        pan_frag.setLayout(pan_fragLayout);
        pan_fragLayout.setHorizontalGroup(
            pan_fragLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        pan_fragLayout.setVerticalGroup(
            pan_fragLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 275, Short.MAX_VALUE)
        );

        pan_principal.add(pan_frag, java.awt.BorderLayout.CENTER);

        getContentPane().add(pan_principal);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void annuler_buttonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_annuler_buttonActionPerformed
    {//GEN-HEADEREND:event_annuler_buttonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_annuler_buttonActionPerformed

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
    private javax.swing.JLabel nom_table;
    private javax.swing.JPanel pan_buttons;
    private javax.swing.JPanel pan_frag;
    private javax.swing.JPanel pan_principal;
    private javax.swing.JButton valider_button;
    // End of variables declaration//GEN-END:variables
}
