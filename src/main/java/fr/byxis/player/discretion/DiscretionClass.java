package fr.byxis.player.discretion;

public class DiscretionClass {

    private double score;
    private boolean eating;
    private int shooting;
    private boolean moving;
    private boolean isListeningMovements;
    private boolean isUsingLights;
    private boolean isUsingCamo;

    public DiscretionClass()
    {
        this.score = 100;
        this.eating = false;
        this.shooting = 0;
        this.moving = false;
        this.isListeningMovements = false;
        this.isUsingLights = false;
        this.isUsingCamo = false;

    }

    public double getScore() {
        return score;
    }

    public void setScore(double _score) {
        this.score = _score;
    }

    public boolean isEating() {
        return eating;
    }

    public void setEating(boolean _eating) {
        this.eating = _eating;
    }

    public boolean isShooting() {
        return shooting > 0;
    }
    public void reduceTimeShooting()
    {
        if (!isShooting())
            return;
        shooting -= 1;
        if (shooting < 0)
            shooting = 0;
    }

    public void setShooting(int _shooting) {
        this.shooting = _shooting;
    }

    public void setMoving(boolean _moving) {
        this.moving = _moving;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isListeningMovements() {
        return isListeningMovements;
    }

    public void setListeningMovements(boolean listeningMovements) {
        isListeningMovements = listeningMovements;
    }

    public boolean isUsingLights() {
        return isUsingLights;
    }

    public void setUsingLights(boolean usingLights) {
        isUsingLights = usingLights;
    }

    public boolean isUsingCamo() {
        return isUsingCamo;
    }

    public void setUsingCamo(boolean usingCamo) {
        isUsingCamo = usingCamo;
    }
}
