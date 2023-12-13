package models;

import utils.Connexion;

import java.sql.*;

public class Luminosite {
    private int id;
    private Date date;

    private Time heure;

    private double niveau;

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

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = heure;
    }

    public double getNiveau() {
        return niveau;
    }

    public void setNiveau(double niveau) {
        this.niveau = niveau;
    }

    public Luminosite[] getPuissance(Connection connection) {
        Luminosite[] luminosites = new Luminosite[9];
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT * FROM luminosite WHERE date=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)){
                preparedStatement.setDate(1, date);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    int index = 0;
                    while (resultSet.next()) {
                        luminosites[index] = new Luminosite();
                        luminosites[index].setHeure(resultSet.getTime("heure"));
                        luminosites[index].setNiveau(resultSet.getDouble("niveau"));
                        index++;
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
        return luminosites;
    }
}
