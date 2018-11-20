package co.sgfc.madgame.Items.General;

import co.sgfc.madgame.Equipment;

public class Roadmap extends Equipment {
    public static final String ID = "ROADMAP";

    public String getID() {
        return ID;
    }

    public Roadmap() {
        super("Roadmap", 10, 0.25);
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}
