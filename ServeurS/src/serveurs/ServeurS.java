
package serveurs;

import java.sql.SQLException;
import java.util.ArrayList;
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
        //Communication_BD com_BD = new Communication_BD(this.parametres.getBD_login(), this.parametres.getBD_mdp(), false);
        
        //Construction des tables qui n'existent pas
        System.out.println("/***Construction des tables***/");
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
        System.out.println("/***Construction des colonnes***/");
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
        System.out.println("/***Récupération des tuples***/");
        Schema_global bd_globale = new Schema_global();
        String tables;
        String attributs;
        String conditions;
        int[] num_serveurs;
        int num_serveur_envoi_requete;
        String[][] tab_conditions;
        for(int i=0; i<tables_nouvelles.length; i++)
        {   
            attributs = "";
            tab_conditions = null;
            conditions = "";
            //Recherche des tuples souhaités
            tables = tables_nouvelles[i];
            attributs_nouveaux = bd_nouvelle.get_liste_attributs_table(tables);
            //Définition des conditions
            if(!bd_nouvelle.get_table_fragmentation(tables_nouvelles[i]).equals("verticale"))
                tab_conditions = bd_nouvelle.get_attributs_fragment(tables, 0);
            else
                conditions = "1=1";
            
            if(bd_globale.get_table_fragmentation(tables_nouvelles[i]).equals("horizontale"))
            {
                System.out.println("HORIZON");
                //Fragmentation horizontale
                //Récupération des serveurs sur lesquels il y a des fragments
                ArrayList<Integer> liste_serveurs = new ArrayList<>();
                liste_serveurs.clear();
                int[] tab_serveurs_par_fragment;
                int nb_fragments = bd_globale.get_nb_fragments(tables);
                for(int j=0; j<nb_fragments; j++)
                {
                    tab_serveurs_par_fragment = bd_globale.get_serveurs_fragment(tables, j);
                    for(int k=0; k<tab_serveurs_par_fragment.length; k++)
                        if(tab_serveurs_par_fragment[k]!=parametres.getNum_serveur() && !liste_serveurs.contains(tab_serveurs_par_fragment[k]))
                            liste_serveurs.add(tab_serveurs_par_fragment[k]);
                }
                //Construction de la requête
                if(liste_serveurs.size()>0)
                {
                    //Attributs
                    for(int j=0; j<attributs_nouveaux.length; j++)
                    {
                        if(!attributs.equals(""))
                            attributs += ", ";
                        attributs += attributs_nouveaux[j];
                    }
                    //Conditions
                    for(int j=0; j<tab_conditions.length; j++)
                    {
                        if(!conditions.equals(""))
                            conditions += " AND ";
                        conditions += tab_conditions[j][0]+""+tab_conditions[j][1]+""+tab_conditions[j][2];
                    }
                    //Envoi de la requête aux serveurs
                    for(int j=0; j<liste_serveurs.size(); j++)
                    {
                        System.out.println("Requete au serveur "+liste_serveurs.get(j)+" : Table "+tables+", attributs : "+attributs);
                        System.out.println("Conditions : "+conditions);
                        /*com_BD.ajoutTuples(this.communication.envoi_requete(liste_serveurs.get(j), tables, attributs, conditions), 
                                tables, bd_nouvelle.get_cles_primaires(tables));*/
                    }
                }
            }
            if(!bd_globale.get_table_fragmentation(tables_nouvelles[i]).equals("horizontale"))
            {
                System.out.println("VERTICON");
                //Fragmentation verticale et hybride
                //On vérifie tous les serveurs pour savoir auxquels demander des tuples
                for(int j=0; j<parametres.getNb_serveurs(); j++)
                {
                    attributs = "";
                    if(tab_conditions!=null)
                        conditions = "";
                    else
                        conditions = "1=1";
                    num_serveur_envoi_requete = parametres.getNum_serveur_distant(j);
                    if(num_serveur_envoi_requete!=parametres.getNum_serveur())
                    {
                        //On parcourt tous les attributs qui seront dans la BD mise à jour
                        for(int k=0; k<attributs_nouveaux.length; k++)
                        {
                            num_serveurs = bd_globale.get_num_serveurs(tables_nouvelles[i], attributs_nouveaux[k]);
                            //Si l'attribut est sur le serveur, on lui demande les tuples
                            for(int l=0; l<num_serveurs.length; l++)
                            {
                                if(num_serveur_envoi_requete==num_serveurs[l])
                                {
                                    //MAJ attributs
                                    if(!attributs.equals(""))
                                        attributs += ", ";
                                    attributs += attributs_nouveaux[k];
                                    //MAJ conditions
                                    if(tab_conditions!=null)
                                    {
                                        for(int m=0; m<tab_conditions.length; m++)
                                        {
                                            System.out.println(tab_conditions[m][0]+" "+attributs_nouveaux[k]);
                                            if(tab_conditions[m][0].equals(attributs_nouveaux[k]))
                                            {
                                                if(!conditions.equals(""))
                                                    conditions += " AND ";
                                                conditions += attributs_nouveaux[k]+""+tab_conditions[m][1]+""+tab_conditions[m][2];
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //Si le serveur a des tuples concernés, on lui envoie une requête
                        if(!attributs.equals(""))
                        {
                            System.out.println("Requete au serveur "+num_serveur_envoi_requete+" : Table "+tables+", attributs : "+attributs);
                            System.out.println("Conditions : "+conditions);
                            /*com_BD.ajoutTuples(this.communication.envoi_requete(num_serveur_envoi_requete, tables, attributs, conditions), 
                                    tables, bd_nouvelle.get_cles_primaires(tables));*/
                        }
                    }
                }
            }
        }
        
        //Attente de la confirmation des autres serveurs
        System.out.println("/***Attente des la confirmations des autres serveurs avant la suppression***/");
        //Sotckage des réponses dans un fichier pour y avoir accès sans bloquer le programme ?
        
        //Suppression des tuples (pour la fragmentation horizontale seulement)
        System.out.println("/***Suppression des tuples***/");
        
        //Suppression des colonnes
        System.out.println("/***Suppression des colonnes***/");
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
        System.out.println("/***Suppression des tables***/");
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
    } 
}
