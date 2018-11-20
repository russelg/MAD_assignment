package co.sgfc.madgame;

abstract public class Food extends Item {
    private double health;

    public Food(String description, double value, double health) {
        super(description, value);
        setHealth(health);
    }

    public double getHealth() {
        return this.health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void use() {
        GameData gameData = GameData.getInstance();
        Player player = gameData.getPlayer();
        player.incrementHealth(getHealth());
    }
}
