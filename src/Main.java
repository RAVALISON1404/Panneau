import models.Secteur;
import utils.Connexion;

import java.sql.Connection;
import java.sql.Date;

public class Main {
    public static void main(String[] args) throws Exception {
        Date date = Date.valueOf("2023-12-11");
        Connection connection = new Connexion().getConnection();
//        Vector<Delestage> delestages = Delestage.all(connection);
//        for (int i = 0; i < delestages.size() - 1; i++) {
//            Date date = delestages.get(i).getDate();
//            System.out.println(delestages.get(i).getSecteur().getPuissanceNecessaire(connection, date));
//        }
        Secteur secteur = new Secteur();
        secteur.setId(1);
        System.out.println(secteur.getCoupure(connection, date).getDebut());
//        System.out.println(secteur.getPuissanceNecessaire(connection, date));
//        System.out.println(secteur.predir(connection, date).getDebut());
        connection.close();
    }
}
