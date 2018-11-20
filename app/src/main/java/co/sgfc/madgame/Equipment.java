package co.sgfc.madgame;

abstract public class Equipment extends Item {
    private double mass;

    public Equipment(String description, int value, double mass) {
        super(description, value);
        setMass(mass);
    }

    public double getMass() {
        return this.mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }
}
