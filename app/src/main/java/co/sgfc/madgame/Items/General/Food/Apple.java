package co.sgfc.madgame.Items.General.Food;

import co.sgfc.madgame.Food;

public class Apple extends Food {
    public static final String ID = "APPLE";

    public String getID() {
        return ID;
    }

    public Apple() {
        super("Apple", 1.50, 15.0);
    }
}
