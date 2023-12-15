package models;

import utils.Connexion;

import java.sql.*;

public class Pointage {
    private int id;
    private Date date;
    private Salle salle;
    private int am;
    private int pm;

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

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public int getAm() {
        return am;
    }

    public void setAm(int am) {
        this.am = am;
    }

    public int getPm() {
        return pm;
    }

    public void setPm(int pm) {
        this.pm = pm;
    }
    public Pointage select (Connection connection) {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT * FROM pointage WHERE salle_id=? AND date=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, salle.getId());
                preparedStatement.setDate(2, date);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    Pointage pointage = null;
                    while (resultSet.next()) {
                        pointage = new Pointage();
                        pointage.setAm(resultSet.getInt("am"));
                        pointage.setPm(resultSet.getInt("pm"));
                        pointage.setId(resultSet.getInt("id"));
                        pointage.setSalle(salle);
                        pointage.setDate(date);
                    }
                    return pointage;
                }
            }
        } catch (Exception e) {
            assert connection != null;
        } finally {
            if (is_connected) {
                assert connection != null;
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    public void insert(Connection connection) throws SQLException {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "INSERT INTO pointage VALUES (default, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDate(1, date);
                preparedStatement.setInt(2, salle.getId());
                preparedStatement.setDouble(3, am);
                preparedStatement.setDouble(4, pm);
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
