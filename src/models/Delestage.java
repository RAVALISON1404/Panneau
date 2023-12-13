package models;

import models.Secteur;

import java.sql.Date;
import java.sql.Time;

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
}
