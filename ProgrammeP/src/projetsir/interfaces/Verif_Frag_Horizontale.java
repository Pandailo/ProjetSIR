/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetsir.interfaces;

import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author yann
 */
public class Verif_Frag_Horizontale extends javax.swing.JFrame {

    /**
     * Creates new form Verif_Frag_Horizontale
     */
    public Verif_Frag_Horizontale(ArrayList<String> list_select) {
        initComponents();
        int cpt=0;
        JOptionPane jop = new JOptionPane();
        String nbFS="";
        String message="Nombre de fragments ?";
        int nbF=0;
        while(!nbFS.matches("^\\d+$") || nbFS.matches("") || nbF>=2 || nbF<=1)
        {    
            nbFS = jop.showInputDialog(null, message, JOptionPane.QUESTION_MESSAGE);
            if(nbFS.matches("^\\d+$") && !nbFS.matches(""))
            {
                nbF=Integer.parseInt(nbFS);
            }
            else
                message="Nombre de fragments doit être supérieur à 2.";
        }
        String splitA[];
        String splitB[];
        String signe;
        String signe2;
        for(int i=0;i<list_select.size();i++)
        {
            splitA=list_select.get(i).split("@");
            for(int j=0;j<splitA.length;i++)
            {
               cpt++;
            }
        }
        GridLayout gd=new GridLayout(nbF,cpt);
        this.pan_dis.setLayout(gd);
        for(int i=0;i<list_select.size();i++)
        {
            splitA=list_select.get(i).split("@");
            for(int j=0;j<splitA.length;i++)
            {
                splitB=splitA[j].split(";");
                signe=splitB[2];
                signe2=this.inverse_signe(signe);
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
    private void initComponents() {

        pan_dis = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout());

        javax.swing.GroupLayout pan_disLayout = new javax.swing.GroupLayout(pan_dis);
        pan_dis.setLayout(pan_disLayout);
        pan_disLayout.setHorizontalGroup(
            pan_disLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        pan_disLayout.setVerticalGroup(
            pan_disLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        getContentPane().add(pan_dis);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private String inverse_signe(String signe)
    {
        String res = "";
        switch(signe)
        {
            case ">" : res = "<="; break;
            case "<" : res = ">="; break;
            case ">=" : res = "<"; break;
            case "<=" : res = ">"; break;
            case "=" : res = "<>"; break;
            case "<>" : res = "="; break;
            case "LIKE" : res = "NOT LIKE"; break;
            case "NOT LIKE" : res = "LIKE"; break;
        }
        return res;
    }
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
            java.util.logging.Logger.getLogger(Verif_Frag_Horizontale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Verif_Frag_Horizontale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Verif_Frag_Horizontale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Verif_Frag_Horizontale.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pan_dis;
    // End of variables declaration//GEN-END:variables
}
