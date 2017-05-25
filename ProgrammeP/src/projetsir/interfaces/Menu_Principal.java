/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.interfaces;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import projetsir.Communication;
import projetsir.Initialisation;
import projetsir.Parametres;
import projetsir.Reinitialisation;
import projetsir.Transformation_global_local;

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

    private void EcrireFichier(File f,String s)
    {
        try
        {
            FileWriter ffw=new FileWriter(f);
            ffw.write(s);
            ffw.close();
        } 
        catch (Exception e) {}
    }
    
    private String lire_fichier(File f) throws IOException
    {
        FileReader in = null;
        String contenu="";
        try
        {
            in = new FileReader(f);
            //Envoi du fichier
            int c;
            contenu = "";
            while((c=in.read())!=-1)
                contenu += (char)c;
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return contenu;
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        Parametrer = new javax.swing.JButton();
        Fragmenter = new javax.swing.JButton();
        Distribuer = new javax.swing.JButton();
        Distribution_serveurs = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        Initialiser = new javax.swing.JButton();
        Reset = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Menu");
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel2.setLayout(new java.awt.GridLayout(2, 2));

        Parametrer.setText("Paramétrer");
        Parametrer.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ParametrerActionPerformed(evt);
            }
        });
        jPanel2.add(Parametrer);

        Fragmenter.setText("Fragmenter");
        Fragmenter.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FragmenterActionPerformed(evt);
            }
        });
        jPanel2.add(Fragmenter);

        Distribuer.setText("Distribuer");
        Distribuer.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DistribuerActionPerformed(evt);
            }
        });
        jPanel2.add(Distribuer);

        Distribution_serveurs.setText("Faire la distribution sur les serveurs");
        Distribution_serveurs.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                Distribution_serveursActionPerformed(evt);
            }
        });
        jPanel2.add(Distribution_serveurs);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(1, 2));

        Initialiser.setText("Initialiser");
        Initialiser.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                InitialiserActionPerformed(evt);
            }
        });
        jPanel3.add(Initialiser);

        Reset.setText("Réinitialiser les bases de données");
        Reset.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ResetActionPerformed(evt);
            }
        });
        jPanel3.add(Reset);

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
                //Suppression des fichiers temporaires
                File repertoire = new File("src/fragments");
                File[] fichiers;
                if(repertoire.exists())
                {
                    fichiers = repertoire.listFiles();
                    for(int i=0; i<fichiers.length; i++)
                        fichiers[i].delete();
                }
                repertoire = new File("src/fragmentation_temporaire");
                if(repertoire.exists())
                {
                    fichiers = repertoire.listFiles();
                    for(int i=0; i<fichiers.length; i++)
                        fichiers[i].delete();
                }
                //Lancement de l'initialisation
                new Initialisation();
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

    private void ResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetActionPerformed
        new Reinitialisation();
        new Transformation_global_local();
        Communication c = new Communication(0);
        c.start();
    }//GEN-LAST:event_ResetActionPerformed

    private void DistribuerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DistribuerActionPerformed
    {//GEN-HEADEREND:event_DistribuerActionPerformed
        File repertoire = new File("src/fragments");
        String[] files=repertoire.list();

        //JOptionPane de choix d'une personne
        JComboBox combo = new JComboBox(files);
        String[] options = { "Distribuer", "Annuler" };
        int selection = JOptionPane.showOptionDialog(null, combo, "Choisir une table pour la distribution", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        //Récupération de la sélection
        if(selection==0) 
        {
            if(combo.getSelectedIndex()>=0)
            {
                String table = combo.getSelectedItem().toString();
                Dlg_distribution dlg = new Dlg_distribution(table);
                dlg.setVisible(true);
            }
        }
    }//GEN-LAST:event_DistribuerActionPerformed

    private void Distribution_serveursActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_Distribution_serveursActionPerformed
    {//GEN-HEADEREND:event_Distribution_serveursActionPerformed
        File repertoire = new File("src/fragmentation_temporaire");
        File[] files=repertoire.listFiles();
        if(files.length<1)
            JOptionPane.showMessageDialog(this, "Il faut avoir fragmenté et distribué au moins une table pour pouvoir lancer la procédue.", 
                    "Erreur", javax.swing.JOptionPane.WARNING_MESSAGE);
        else
        {
            String ajoutF="{\n\t\"tables\":\n\t[\n";
            String finFich="\n\t]\n}";
            FileOutputStream fos = null;
            for(int i=0;i<files.length;i++)
            {
                try {
                    ajoutF+=this.lire_fichier(files[i]);
                } catch (IOException ex) {
                    Logger.getLogger(Fragmenter.class.getName()).log(Level.SEVERE, null, ex);
                }
               if(i!=files.length-1)
                   ajoutF+=",\n";
            }
            ajoutF+=finFich;
            this.EcrireFichier(new File(new Parametres().get_chemin_schemas()+"/global.json"), ajoutF);
            new Transformation_global_local();
            Communication c = new Communication(0);
            c.start();
        }
    }//GEN-LAST:event_Distribution_serveursActionPerformed

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
    private javax.swing.JButton Distribuer;
    private javax.swing.JButton Distribution_serveurs;
    private javax.swing.JButton Fragmenter;
    private javax.swing.JButton Initialiser;
    private javax.swing.JButton Parametrer;
    private javax.swing.JButton Reset;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
