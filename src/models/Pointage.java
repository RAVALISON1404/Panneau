package models;

import models.Salle;
import utils.Connexion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    public int update(Connection connection) {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "UPDATE pointage SET am=?, pm=? WHERE salle_id=? AND date=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDouble(1, am);
            preparedStatement.setDouble(2, pm);
            preparedStatement.setInt(3, id);
            preparedStatement.setDate(4, date);
            System.out.println(preparedStatement);
            return preparedStatement.executeUpdate();
        } catch (Exception e) {
            assert connection != null;
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
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
        return 0;
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
