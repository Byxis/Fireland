package fr.byxis.discretion;

public class DiscretionClass {

    private double score;
    private boolean eating;
    private boolean shooting;
    private boolean moving;
    private boolean isListeningMovements;

    public DiscretionClass()
    {
        this.score = 100;
        this.eating = false;
        this.shooting = false;
        this.moving = false;
        this.isListeningMovements = false;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isEating() {
        return eating;
    }

    public void setEating(boolean eating) {
        this.eating = eating;
    }

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
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
}
