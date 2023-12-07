package models.historique;

import models.Secteur;

import java.sql.Date;
import java.sql.Time;

public class Consommation {
    private Secteur secteur;
    private Date date;
    private Time heure;
    private double panneau;
    private double batterie;

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
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

    public double getPanneau() {
        return panneau;
    }

    public void setPanneau(double panneau) {
        this.panneau = panneau;
    }

    public double getBatterie() {
        return batterie;
    }

    public void setBatterie(double batterie) {
        this.batterie = batterie;
    }
}
