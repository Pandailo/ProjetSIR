
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
        /*Communication_BD com = new Communication_BD("ag092850", "ag092850", false);
        CachedRowSet crs = com.chargerTable("test", "*", "1=1");
        Communication_BD com2 = new Communication_BD("aj205896", "aj205896", false); 
        try {
            crs.first();
            //crs.updateInt("id", 1);
            //com2.ajoutColonne("test", "att1", "VARCHAR2(30)");
            String[] pk = new String[1];
            pk[0] = "id";
            //pk[1] = "id2";
            com2.ajoutTuples(crs, "test", pk);
        } catch (SQLException ex) {
            Logger.getLogger(ServeurS.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        Parametres parametres = new Parametres();
        //p.ecriture_parametres();
        Schema_local bd_actuelle = new Schema_local(true);
        Schema_local bd_nouvelle = new Schema_local(false);
        //TODO: automatisation pour savoir si la co se fait à la fac ou non
        //Communication_BD com_BD = new Communication_BD(parametres.getBD_login(), parametres.getBD_mdp(), false);
        
        //Construction des tables qui n'existent pas
        String[] tables_actuelles = bd_actuelle.get_liste_nom_tables();
        String[] tables_nouvelles = bd_nouvelle.get_liste_nom_tables();
        String[] attributs_nouveaux;
        String creation_attributs;
        String creation_pk;
        int taille_creation_pk;
        boolean creation;
        for(int i=0; i<tables_nouvelles.length; i++)
        {
            creation = true;
            creation_attributs = "";
            creation_pk = "PRIMARY KEY(";
            taille_creation_pk = creation_pk.length();
            for(int j=0; j<tables_actuelles.length; j++)
                if(tables_nouvelles[i].equals(tables_actuelles[j]))
                {
                    creation = false;
                    j = tables_actuelles.length;
                }
            if(creation)
            {
                attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables_nouvelles[i]);
                for(int j=0; j<attributs_nouveaux.length; j++)
                {
                    creation_attributs += attributs_nouveaux[j]+" "+bd_nouvelle.get_type_attribut(tables_nouvelles[i], attributs_nouveaux[j])+", ";
                    if(bd_nouvelle.is_primary_key(tables_nouvelles[i], attributs_nouveaux[j]))
                    {
                        if(creation_pk.length()!=taille_creation_pk)
                            creation_pk += ", ";
                        creation_pk += attributs_nouveaux[j];
                    }
                }
                creation_attributs += creation_pk+")";
                System.out.println("Création de la table "+tables_nouvelles[i]+".");
                System.out.println(creation_attributs);
                //com_BD.ajoutTable(tables_nouvelles[i], null);
            }
        }
        
        //Construction des colonnes qui n'existent pas
        String[] attributs_actuels;
        for(int i=0; i<tables_nouvelles.length; i++)
        {
            creation = false;
            for(int j=0; j<tables_actuelles.length; j++)
                if(tables_nouvelles[i].equals(tables_actuelles[j]))
                {
                    creation = true;
                    j = tables_actuelles.length;
                }
            if(creation)
            {
                attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables_nouvelles[i]);
                attributs_actuels = bd_actuelle.get_liste_attributs_table(tables_nouvelles[i]);
                for(int j=0; j<attributs_nouveaux.length; j++)
                {
                    creation = true;
                    for(int k=0; k<attributs_actuels.length; k++)
                        if(attributs_nouveaux[j].equals(attributs_actuels[k]))
                        {
                            creation = false;
                            k = attributs_actuels.length;
                        }
                    if(creation)
                    {
                        System.out.println("Ajout de la colonne "+attributs_nouveaux[j]+" type "+bd_nouvelle.get_type_attribut(tables_nouvelles[i], attributs_nouveaux[j])+" à la table "+tables_nouvelles[i]+".");
                        //com_BD.ajoutColonne(tables_nouvelles[i], attributs_nouveaux[j], bd_nouvelle.get_type_attribut(tables_nouvelles[i], attributs_nouveaux[j]));
                    }
                }
            }
        }
        
        //Récupération des tuples manquants
        
        //Attente de la confirmation des autres serveurs
        //Sotckage des réponses dans un fichier pour y avoir accès sans bloquer le programme ?
        
        //Suppression des tuples
        
        //Suppression des colonnes
        boolean suppression;
        for(int i=0; i<tables_nouvelles.length; i++)
        {
            suppression = false;
            for(int j=0; j<tables_actuelles.length; j++)
                if(tables_nouvelles[i].equals(tables_actuelles[j]))
                {
                    suppression = true;
                    j = tables_actuelles.length;
                }
            if(suppression)
            {
                attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables_nouvelles[i]);
                attributs_actuels = bd_actuelle.get_liste_attributs_table(tables_nouvelles[i]);
                for(int j=0; j<attributs_actuels.length; j++)
                {
                    suppression = true;
                    for(int k=0; k<attributs_nouveaux.length; k++)
                        if(attributs_actuels[j].equals(attributs_nouveaux[k]))
                        {
                            suppression = false;
                            k = attributs_actuels.length;
                        }
                    if(suppression)
                    {
                        System.out.println("Suppression de la colonne "+attributs_nouveaux[j]+" de la table "+tables_nouvelles[i]+".");
                        //com_BD.suppressionColonne(tables_nouvelles[i], attributs_nouveaux[j]);
                    }
                }
            }
        }
        
        //Suppression des tables
        for(int i=0; i<tables_actuelles.length; i++)
        {
            suppression = true;
            for(int j=0; j<tables_nouvelles.length; j++)
                if(tables_actuelles[i].equals(tables_nouvelles[j]))
                {
                    suppression = false;
                    j = tables_nouvelles.length;
                }
            if(suppression)
            {
                System.out.println("Suppression de la table "+tables_actuelles[i]+".");
                //com_BD.suppressionTable(tables_nouvelles[i]);
            }
        }
        
        //MAJ du fichier BD_actuelle.json
        /*File source = new File(this.parametres.getChemin_schemas()+"/local.json");
        File dest = new File(this.parametres.getChemin_schemas()+"/BD_actuelle.json");
        this.copier_fichier(source, dest);*/
    } 
}
