package co.sgfc.madgame.Items.General.Food;

import co.sgfc.madgame.Food;

public class Banana extends Food {
    public static final String ID = "BANANA";

    public String getID() {
        return ID;
    }

    public Banana() {
        super("Banana", 1.50, 10.0);
    }
}
