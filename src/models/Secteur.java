package models;

import models.historique.Consommation;
import models.historique.Delestage;
import utils.Connexion;

import java.sql.*;
import java.util.Vector;

public class Secteur {
    private int id;
    private String nom;
    private Source source;

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

    public Source getSource() {
        try (Connection connection = new Connexion().getConnection()) {
            String sql = "SELECT * FROM source where id=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, source.getId());
                System.out.println(statement);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        source.setBatterie(resultSet.getDouble("batterie"));
                        source.setPanneau(resultSet.getDouble("panneau"));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Vector<Delestage> predir(Date date) {
        return null;
    }

    public Consommation[] getConsommation(Connection connection, Date date) {
        Consommation[] consommations = new Consommation[10];
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT * FROM consommation_necessaire WHERE secteur=? AND date=? ORDER BY heure";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setDate(2, date);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    int index = 0;
                    while (resultSet.next()) {
                        consommations[index] = new Consommation();
                        consommations[index].setSecteur(this);
                        consommations[index].setDate(date);
                        consommations[index].setHeure(resultSet.getTime("heure"));
                        consommations[index].setPanneau(resultSet.getDouble("panneau"));
                        consommations[index].setBatterie(resultSet.getDouble("batterie"));
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
        return consommations;
    }

    public Delestage getCoupure(Connection connection, Date date) {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            Consommation[] consommations = getConsommation(connection, date);
            double consoTotal = 0;
            for (Consommation consommation : consommations) {
                if (consommation.getBatterie() > 0) {
                    consoTotal += consommation.getBatterie();
                }
                if (consoTotal > getSource().getBatterie() / 2) {
                    double reste = consoTotal - getSource().getBatterie() / 2;
                    reste = reste * 60 / consommation.getBatterie();
                    reste = 60 - reste;
                    Delestage delestage = new Delestage();
                    delestage.setDebut(new Time(consommation.getHeure().getHours(), (int) reste, consommation.getHeure().getSeconds()));
                    delestage.setFin(new Time(17, 0, 0));
                    return delestage;
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
        return null;
    }

    public double getPuissance(Connection connection, Date date) {
        double puissance = 0;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT puissance FROM besoin WHERE secteur_id=? AND date=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
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
        return puissance;
    }

    public void setPuissance(Connection connection, Date date, double puissance) {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "UPDATE besoin SET puissance=? WHERE date=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, puissance);
                statement.setDate(2, date);
                System.out.println(statement);
                statement.executeUpdate();
            }
            connection.commit();
        } catch (Exception e) {
            try {
                assert connection != null;
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
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
    }

    public double getPuissanceNecessaire(Connection connection, Date date) {
        double puissance = 0;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            Delestage coupure = getCoupure(connection, date);
            Vector<Delestage> delestages = getDelestages(connection, date);
            for (Delestage delestage : delestages) {
                if (coupure == null) {
                    puissance = findPuissance(connection, date, coupure, delestage);
                }
                else if (!coupure.getDebut().equals(delestage.getDebut())) {
                    puissance = findPuissance(connection, date, coupure, delestage);
                } else {
                    puissance = getPuissance(connection, date);
                }
                break;
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
        return puissance;
    }

    public double findPuissance(Connection connection, Date date, Delestage coupure, Delestage delestage) {
        double puissance;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            double puissance_initiale = getPuissance(connection, date);
            int diviseur = 2;
            if (coupure == null) {
                while (coupure == null) {
                    puissance = getPuissance(connection, date);
                    puissance += (puissance_initiale / diviseur);
                    setPuissance(connection, date, puissance);
                    coupure = getCoupure(connection, date);
                }
            }
            while (!coupure.getDebut().equals(delestage.getDebut())) {
                puissance = getPuissance(connection, date);
                if (coupure.getDebut().before(delestage.getDebut())) {
                    puissance -= (puissance_initiale / diviseur);
                } else {
                    puissance += (puissance_initiale / diviseur);
                }
                setPuissance(connection, date, puissance);
                diviseur *= 2;
                coupure = getCoupure(connection, date);
                while (coupure == null) {
                    puissance = getPuissance(connection, date);
                    puissance += (puissance_initiale / diviseur);
                    setPuissance(connection, date, puissance);
                    coupure = getCoupure(connection, date);
                    diviseur *= 2;
                }
            }
            puissance = getPuissance(connection, date);
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
        return puissance;
    }

    public Vector<Delestage> getDelestages(Connection connection, Date date) {
        Vector<Delestage> delestages;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT debut, fin FROM delestage WHERE secteur_id=? AND date=? ORDER BY debut";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setDate(2, date);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    delestages = new Vector<>();
                    while (resultSet.next()) {
                        Delestage delestage = new Delestage();
                        delestage.setSecteur(this);
                        delestage.setDebut(resultSet.getTime("debut"));
                        delestage.setFin(resultSet.getTime("fin"));
                        delestage.setDate(date);
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