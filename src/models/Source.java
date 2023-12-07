package models;

public class Source {
    private int id;
    private double panneau;
    private double batterie;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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