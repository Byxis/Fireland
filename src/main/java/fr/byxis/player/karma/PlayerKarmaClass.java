package fr.byxis.player.karma;

public class PlayerKarmaClass {

    private double rang;
    private double max;

    public PlayerKarmaClass(double rang, double max)
    {
        this.rang = rang;
        this.max = max;
    }

    public double getRang() {
        return rang;
    }

    public void setRang(double rang) {
        this.rang = rang;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }
}
