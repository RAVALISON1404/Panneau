import models.Secteur;
import models.Source;

import java.sql.Date;

public class Main {
    public static void main(String[] args) {
        Date date = Date.valueOf("2023-12-06");
        Secteur secteur = new Secteur();
        secteur.setId(1);
        Source source = new Source();
        source.setId(1);
        secteur.setSource(source);
        System.out.println(secteur.getCoupure(null, date).getDebut());
        System.out.println(secteur.getPuissanceNecessaire(null, date));
    }
}