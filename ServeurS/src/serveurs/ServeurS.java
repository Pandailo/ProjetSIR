
package serveurs;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;

public class ServeurS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*Gestion_json g_json = new Gestion_json("src/parametres/config.json");
        g_json.ouverture_json();
        String login = (String)g_json.get_attribut("BD_login");
        System.out.println(login);
        int num = (int)(long)g_json.get_attribut_tableau("serveurs", 1, "num");
        System.out.println(num);*/
        /*Communication c = new Communication();
        c.demarrer_serveur();*/
        //c.envoi_schemas();*/
        //Schema_global sg = new Schema_global();
        Communication_BD com = new Communication_BD("ag092850", "ag092850", false);
        CachedRowSet crs = com.chargerTable("test", "*", "1=1");
        Communication_BD com2 = new Communication_BD("aj205896", "aj205896", false); 
        try {
            crs.first();
            //crs.updateInt("id", 1);
            //com2.ajoutColonne("test", "att1", "VARCHAR2(30)");
            com2.ajoutTuples(crs, "test", "id");
        } catch (SQLException ex) {
            Logger.getLogger(ServeurS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}
