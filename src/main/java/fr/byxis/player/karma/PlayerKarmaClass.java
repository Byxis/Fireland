package fr.byxis.player.karma;

public class PlayerKarmaClass {

    private double rang;
    private double max;

    public PlayerKarmaClass(double _rang, double _max)
    {
        this.rang = _rang;
        this.max = _max;
    }

    public double getRang() {
        return rang;
    }

    public void setRang(double _rang) {
        this.rang = _rang;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double _max) {
        this.max = _max;
    }
}
