package co.sgfc.madgame.Items.General.Food;

import co.sgfc.madgame.Food;

public class PoisonIvy extends Food {
    public static final String ID = "POISON_IVY";

    public String getID() {
        return ID;
    }

    public PoisonIvy() {
        super("Poison Ivy", 5, -10.00);
    }
}
