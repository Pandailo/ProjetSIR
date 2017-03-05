
package serveurs;

import javax.swing.JOptionPane;

public class ServeurS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*Object[] options = {"Serveur","Client"};
        int n = JOptionPane.showOptionDialog(null,"Serveur ou client ?",
            "Serveur/Client",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
        if(n==JOptionPane.YES_OPTION)
        {
            Communication_serveur cs = new Communication_serveur(1234, "src/schemas");
            cs.run();
        }
        else
            if(n==JOptionPane.NO_OPTION)
            {
                Communication_client cc = new Communication_client("127.0.0.1", 1234, 0, 1, "src/schemas");
                cc.run();
            }*/
        /*Gestion_json g_json = new Gestion_json("src/parametres/config.json");
        g_json.ouverture_json();
        String login = (String)g_json.get_attribut("BD_login");
        System.out.println(login);
        int num = (int)(long)g_json.get_attribut_tableau("serveurs", 1, "num");
        System.out.println(num);*/
        Communication c = new Communication("src/parametres/config.json");
        c.demarrer_serveur();
    }   
}
