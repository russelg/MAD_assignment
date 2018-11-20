package co.sgfc.madgame.Items.General.Food;

import co.sgfc.madgame.Food;

public class Pie extends Food {
    public static final String ID = "PIE";

    public String getID() {
        return ID;
    }

    public Pie() {
        super("Raspberry Pie", 2, 10.00);
    }
}
