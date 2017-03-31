/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.awt.GridLayout;
import javax.swing.*;

/**
 *
 * @author yv965015
 */
public class DLG_Frag_Verticale extends javax.swing.JFrame {
    Parametres param;
    /**
     * Creates new form DLG_Frag_Verticale
     */
    public DLG_Frag_Verticale(String table) {
        JOptionPane jop = new JOptionPane();
        String nbQ="";
        String message="Nombre de requêtes ?";
        int nbR=0;
        while(!nbQ.matches("^\\d+$")||nbQ.matches(""))
        {    
            nbQ = jop.showInputDialog(null, message, JOptionPane.QUESTION_MESSAGE);
            if(nbQ.matches("^\\d+$"))
            {
                nbR=Integer.parseInt(nbQ);
            }
            else
                message="Nombre de requêtes en nombre positif ? ";
        }
        param=new Parametres();
        int nbS=param.get_nb_serveurs();
        bd_globale bdg=new bd_globale();
        String[] liste_att=bdg.get_liste_attributs_table(table);
        int nbA=liste_att.length;
        initComponents();
        nbR++;
        nbS++;
        nbA++;
        Pan_Dis.setLayout(new GridLayout(nbR,nbS));
        Pan_Ut.setLayout(new GridLayout(nbR,nbA));
        this.creationMatDis(nbR, nbS,liste_att);
        this.creationMatUt(nbR, nbA,liste_att);
        
            
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        Pan_Ut = new javax.swing.JPanel();
        Pan_Dis = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        Quitter_button = new javax.swing.JButton();
        Lancer_button = new javax.swing.JButton();

        setTitle("Fragmentation verticale");
        setPreferredSize(new java.awt.Dimension(900, 600));

        jPanel1.setLayout(new java.awt.GridLayout(1, 2));

        Pan_Ut.setLayout(new java.awt.GridLayout(1, 0));
        jPanel1.add(Pan_Ut);

        javax.swing.GroupLayout Pan_DisLayout = new javax.swing.GroupLayout(Pan_Dis);
        Pan_Dis.setLayout(Pan_DisLayout);
        Pan_DisLayout.setHorizontalGroup(
            Pan_DisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        Pan_DisLayout.setVerticalGroup(
            Pan_DisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 259, Short.MAX_VALUE)
        );

        jPanel1.add(Pan_Dis);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Matrice Utilisation");
        jLabel1.setToolTipText("");
        jPanel2.add(jLabel1);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Matrice Distribution");
        jPanel2.add(jLabel2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel3.setMinimumSize(new java.awt.Dimension(380, 50));
        jPanel3.setPreferredSize(new java.awt.Dimension(400, 50));
        jPanel3.setLayout(new java.awt.GridLayout(1, 2));

        Quitter_button.setText("Quitter fragmentation");
        Quitter_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Quitter_buttonActionPerformed(evt);
            }
        });
        jPanel3.add(Quitter_button);

        Lancer_button.setText("Lancer fragmentation");
        Lancer_button.setToolTipText("");
        jPanel3.add(Lancer_button);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Quitter_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Quitter_buttonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_Quitter_buttonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DLG_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DLG_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DLG_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DLG_Frag_Verticale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        
    }
    void creationMatDis(int nbR,int nbS,String[] liste_att)
    {
        for(int i=0;i<nbR;i++)
        {
            if(i==0)
            {   
                for(int j=0;j<nbS;j++)
                {
                    if(j==0)
                    {
                        JLabel nomA=new JLabel();
                        nomA.setText("");
                        Pan_Dis.add(nomA);
                    }
                    else
                    {
                        JLabel nomA=new JLabel();
                        nomA.setText("Serveur :"+param.get_num_serveur(j-1));
                        Pan_Dis.add(nomA);
                    }
                    
                }
            }
            else
            {
                for(int j=0;j<nbS;j++)
                {
                    if(j==0)
                    {
                        JLabel nomA=new JLabel();
                        nomA.setText("Requete "+i);
                        Pan_Dis.add(nomA); 
                        
                    }
                    else
                    {
                        SpinnerModel model = new SpinnerNumberModel();
                        JSpinner spin=new JSpinner(model);
                        Pan_Dis.add(spin);
                    }
                }
            }
                
           
        }
        
        
    }
    void creationMatUt(int nbR,int nbA,String[] liste_att)
    {
        for(int i=0;i<nbR;i++)
        {
            if(i==0)
            { 
                for(int j=0;j<nbA;j++)
                {
                    if(j==0)
                    {
                        JLabel nomA=new JLabel();
                        nomA.setText("");
                        Pan_Ut.add(nomA);
                    }
                    else
                    {
                       JLabel nomA=new JLabel();
                        nomA.setText("Attribut :"+liste_att[j-1]);
                        Pan_Ut.add(nomA); 
                    }
                }
            }
            else
            {
                 for(int j=0;j<nbA;j++)
                {
                    if(j==0)
                    {
                        JLabel nomA=new JLabel();
                        nomA.setText("Requete "+i);
                        Pan_Ut.add(nomA); 
                    }
                    else
                    {
                        JCheckBox ch=new JCheckBox();
                        Pan_Ut.add(ch);
                    }
                }
            }
            
        }
        
        
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Lancer_button;
    private javax.swing.JPanel Pan_Dis;
    private javax.swing.JPanel Pan_Ut;
    private javax.swing.JButton Quitter_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
