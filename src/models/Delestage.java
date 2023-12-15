package models;

import models.Secteur;
import utils.Connexion;

import java.sql.*;
import java.util.Vector;

public class Delestage {
    private int id;
    private Date date;
    private Time debut;
    private Time fin;
    private Secteur secteur;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getDebut() {
        return debut;
    }

    public void setDebut(Time debut) {
        this.debut = debut;
    }

    public Time getFin() {
        return fin;
    }

    public void setFin(Time fin) {
        this.fin = fin;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public static Vector<Delestage> all(Connection connection) {
        Vector<Delestage> delestages;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT id, date, debut, fin, secteur_id FROM delestage";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    delestages = new Vector<>();
                    while (resultSet.next()) {
                        Delestage delestage = new Delestage();
                        delestage.setId(resultSet.getInt("id"));
                        delestage.setDate(resultSet.getDate("date"));
                        delestage.setDebut(resultSet.getTime("debut"));
                        delestage.setFin(resultSet.getTime("fin"));
                        Secteur s = new Secteur();
                        s.setId(resultSet.getInt("secteur_id"));
                        delestage.setSecteur(s);
                        delestages.add(delestage);
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
        return delestages;
    }
}
