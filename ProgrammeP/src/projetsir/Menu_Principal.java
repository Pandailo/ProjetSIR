/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir;

import java.io.File;
import javax.swing.JOptionPane;

/**
 *
 * @author yv965015
 */
public class Menu_Principal extends javax.swing.JFrame {

    /**
     * Creates new form Menu_Principal
     */
    public Menu_Principal() {
        initComponents();
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
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        Parametrer = new javax.swing.JButton();
        Fragmenter = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        Initialiser = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Menu");
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.GridLayout(1, 2));

        Parametrer.setText("Paramétrer");
        Parametrer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ParametrerActionPerformed(evt);
            }
        });
        jPanel2.add(Parametrer);

        Fragmenter.setText("Fragmenter");
        Fragmenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FragmenterActionPerformed(evt);
            }
        });
        jPanel2.add(Fragmenter);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        Initialiser.setText("Initialiser");
        Initialiser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InitialiserActionPerformed(evt);
            }
        });
        jPanel3.add(Initialiser);

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void FragmenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FragmenterActionPerformed
        Fragmenter f=new Fragmenter();
        f.setVisible(true);
    }//GEN-LAST:event_FragmenterActionPerformed

    private void InitialiserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InitialiserActionPerformed
        //Désactivation des boutons
        this.Initialiser.setEnabled(false);
        this.Parametrer.setEnabled(false);
        this.Fragmenter.setEnabled(false);
        JOptionPane jop = new JOptionPane();    	
        int option = jop.showConfirmDialog(null, "Voulez-vous lancer l'initialisation ?", "Lancement de l'initialisation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if(option == JOptionPane.OK_OPTION)
        {
            Parametres parametres = new Parametres();
            File f = new File(parametres.get_chemin_schemas()+"/global.json");
            if(f.exists())
                option = jop.showConfirmDialog(null, "Il semble qu'une initialisation ait déjà été faite, êtes-vous sûr de vouloir continuer ? (résultats non garantis)", "Lancement de l'initialisation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(option==JOptionPane.OK_OPTION)
            {
                Initialisation initialisation = new Initialisation();
                System.out.println("Initialisation du schéma terminée.");
                Communication communication = new Communication(1);
                communication.start();
                System.out.println("Envoi du schéma terminée.");
                jop.showMessageDialog(null, "Initialisation terminée !", "Fin de l'initialisation", JOptionPane.INFORMATION_MESSAGE, null);
            }
            else
                jop.showMessageDialog(null, "Initialisation anulée.", "Fin de l'initialisation", JOptionPane.INFORMATION_MESSAGE, null);
        }
        //Réactivation des boutons
        this.Initialiser.setEnabled(true);
        this.Parametrer.setEnabled(true);
        this.Fragmenter.setEnabled(true);
    }//GEN-LAST:event_InitialiserActionPerformed

    private void ParametrerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ParametrerActionPerformed
        Dlg_parametres dlg = new Dlg_parametres();
        dlg.setVisible(true);
    }//GEN-LAST:event_ParametrerActionPerformed

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
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Menu_Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Menu_Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Fragmenter;
    private javax.swing.JButton Initialiser;
    private javax.swing.JButton Parametrer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
