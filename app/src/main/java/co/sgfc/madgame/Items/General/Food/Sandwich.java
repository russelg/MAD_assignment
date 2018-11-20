package co.sgfc.madgame.Items.General.Food;

import co.sgfc.madgame.Food;

public class Sandwich extends Food {
    public static final String ID = "SANDWICH";

    public String getID() {
        return ID;
    }

    public Sandwich() {
        super("Sandwich", 5, 20.00);
    }
}
