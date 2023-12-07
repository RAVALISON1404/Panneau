package models;

import models.historique.NbrPers;
import utils.Connexion;

import java.sql.*;

public class Salle {
    private int id;
    private String nom;
    private Secteur secteur;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

//    nbr de personne en moyenne par rapport a une date données
    public NbrPers nbr_pers(Connection connection, Date date) {
        NbrPers nbrPers = null;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT AVG(am) AS am, AVG(pm) AS pm FROM nbr_pers WHERE salle_id=? AND EXTRACT(DOW FROM date) = EXTRACT(DOW FROM ?::date)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setDate(2, date);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        nbrPers = new NbrPers();
                        nbrPers.setAm(resultSet.getInt("am"));
                        nbrPers.setPm(resultSet.getInt("pm"));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (is_connected) {
                try {
                    assert connection != null;
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return nbrPers;
    }
}