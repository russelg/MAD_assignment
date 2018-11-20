package co.sgfc.madgame.Items.General.Food;

import co.sgfc.madgame.Food;

public class Grass extends Food {
    public static final String ID = "GRASS";

    public String getID() {
        return ID;
    }

    public Grass() {
        super("Grass", 0.05, 1.0);
    }
}
