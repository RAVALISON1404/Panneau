package models;

import utils.Connexion;

import java.sql.*;
import java.util.Vector;

public class Secteur {
    private int id;
    private String nom;

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

    public double getBatterie(Connection connection) {
        double batterie = 0;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT batterie FROM secteur WHERE id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        batterie = resultSet.getDouble("batterie");
                    }
                }
            }
            return batterie;
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
    }

    public double getPanneau(Connection connection) {
        double panneau = 0;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT panneau FROM secteur WHERE id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        panneau = resultSet.getDouble("panneau");
                    }
                }
            }
            return panneau;
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
    }

    public Delestage predir(Connection connection, Date date) {
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            Besoin besoin = new Besoin();
            besoin.setDate(date);
            besoin.setSecteur(this);
            besoin = besoin.getPuissanceMoyenne(connection);
            Besoin newBesoin = besoin.select(connection);
            if (newBesoin == null) {
                besoin.insert(connection);
            }
            Vector<Salle> salles = getSalles(connection);
            Vector<Pointage> pointages = new Vector<>();
            for (Salle salle : salles) {
                pointages.add(salle.getPointage(connection, date));
            }
            for (Pointage p : pointages) {
                Pointage pointage = p.select(connection);
                if (pointage == null) {
                    p.insert(connection);
                }
            }
            connection.commit();
            return getCoupure(connection, date);
        } catch (Exception e) {
            try {
                assert connection != null;
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
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

    public Vector<Consommation> getConsommation(Connection connection, Date date) {
        Vector<Consommation> consommations = new Vector<>();
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
                    while (resultSet.next()) {
                        Consommation consommation = new Consommation();
                        consommation.setSecteur(this);
                        consommation.setDate(date);
                        consommation.setHeure(resultSet.getTime("heure"));
                        consommation.setPanneau(resultSet.getDouble("panneau"));
                        consommation.setBatterie(resultSet.getDouble("batterie"));
                        consommations.add(consommation);
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
            Vector<Consommation> consommations = getConsommation(connection, date);
            double consoTotal = 0;
            double batterie = getBatterie(connection);
            for (Consommation consommation : consommations) {
                consoTotal += consommation.getBatterie();
                if (consoTotal < 0) {
                    consoTotal = 0;
                }
                if (consoTotal > (batterie / 2)) {
                    double reste = consoTotal - batterie / 2;
                    reste = reste * 60 / consommation.getBatterie();
                    reste = 60 - reste;
                    Delestage delestage = new Delestage();
                    delestage.setDate(date);
                    delestage.setDebut(new Time(consommation.getHeure().getHours(), (int) reste, consommation.getHeure().getSeconds()));
                    delestage.setFin(new Time(18, 0, 0));
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
            String sql = "UPDATE besoin SET puissance=? WHERE date=? AND secteur_id=?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, puissance);
                statement.setDate(2, date);
                statement.setInt(3, id);
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
                    puissance = findPuissance(connection, date, null, delestage);
                } else if (!coupure.getDebut().equals(delestage.getDebut())) {
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
            while (coupure == null) {
                puissance = getPuissance(connection, date);
                puissance += (puissance_initiale / diviseur);
                setPuissance(connection, date, puissance);
                coupure = getCoupure(connection, date);

            }
            while (!coupure.getDebut().equals(delestage.getDebut())) {
                if (diviseur != 0) {
                    puissance = getPuissance(connection, date);
                    if (coupure.getDebut().before(delestage.getDebut())) {
                        puissance -= (puissance_initiale / diviseur);
                    } else if (coupure.getDebut().after(delestage.getDebut()) || coupure == null) {
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
                } else {
                    break;
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

    public Vector<Salle> getSalles(Connection connection) throws SQLException {
        Vector<Salle> salles = new Vector<>();
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT * FROM salle WHERE secteur_id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Salle salle = new Salle();
                        salle.setId(resultSet.getInt("id"));
                        salle.setNom(resultSet.getString("nom"));
                        salle.setSecteur(this);
                        salles.add(salle);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (is_connected) {
                assert connection != null;
                connection.close();
            }
        }
        return salles;
    }

    public static Vector<Secteur> all(Connection connection) {
        Vector<Secteur> secteurs;
        boolean is_connected = false;
        try {
            if (connection == null) {
                is_connected = true;
                connection = new Connexion().getConnection();
            }
            String sql = "SELECT * FROM secteur";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                System.out.println(preparedStatement);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    secteurs = new Vector<>();
                    while (resultSet.next()) {
                        Secteur secteur = new Secteur();
                        secteur.setId(resultSet.getInt("id"));
                        secteur.setNom(resultSet.getString("nom"));
                        secteurs.add(secteur);
                    }
                }
                return secteurs;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
    }
}