
package serveurs;

import javax.swing.JOptionPane;

public class ServeurS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Object[] options = {"Serveur","Client"};
        int n = JOptionPane.showOptionDialog(null,"Serveur ou client ?",
            "Serveur/Client",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
        if(n==JOptionPane.YES_OPTION)
        {
            Communication_Serveur cs = new Communication_Serveur(1234, "src/schemas");
            cs.run();
        }
        else
            if(n==JOptionPane.NO_OPTION)
            {
                Communication_Client cc = new Communication_Client("127.0.0.1", 1234, 0, 1, "src/schemas");
                cc.run();
            }
    }
    
}
