package co.sgfc.madgame.Items.General.Food;

import co.sgfc.madgame.Food;

public class Chips extends Food {
    public static final String ID = "CHIPS";

    public String getID() {
        return ID;
    }

    public Chips() {
        super("Chips", 3, 5.00);
    }
}
