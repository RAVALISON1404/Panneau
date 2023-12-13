package models;

import utils.Connexion;

import java.sql.*;

public class Besoin {
    private int id;
    private Date date;
    private double puissance;
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

    public double getPuissance() {
        return puissance;
    }

    public void setPuissance(double puissance) {
        this.puissance = puissance;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public Besoin getPuissanceMoyenne(Connection connection) {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT avg(puissance) as puissance FROM besoin WHERE secteur_id=? AND date<? ";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, secteur.getId());
                preparedStatement.setDate(2, date);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        puissance = resultSet.getDouble("puissance");
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
        return this;
    }

    public int update(Connection connection) throws SQLException {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "UPDATE besoin SET puissance=? WHERE secteur_id=? AND date=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDouble(1, puissance);
                preparedStatement.setInt(2, secteur.getId());
                preparedStatement.setDate(3, date);
                System.out.println(preparedStatement);
                return preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            assert connection != null;
            connection.rollback();
        } finally {
            if (is_connected) {
                assert connection != null;
                connection.close();

            }
        }
        return 0;
    }

    public void insert(Connection connection) throws SQLException {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "INSERT INTO besoin VALUES (default, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDate(1, date);
                preparedStatement.setDouble(2, puissance);
                preparedStatement.setInt(3, secteur.getId());
                System.out.println(preparedStatement);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            assert connection != null;
            connection.rollback();
        } finally {
            if (is_connected) {
                assert connection != null;
                connection.close();

            }
        }
    }
}
