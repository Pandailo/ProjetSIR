
package serveurs;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JoinRowSetImpl;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import serveurs.arbre_requetes.Noeud;
import serveurs.communications.Communication_BD;
import serveurs.interfaces.Menu;

public class ServeurS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Parametres p = new Parametres();
        //p.ecriture_parametres();
        //Communication c = new Communication();
        //c.demarrer_serveur();
        //Communication_BD bd = new Communication_BD("ag092850", "ag092850");
        Menu menu=new Menu();
        menu.setVisible(true);
    } 
}
