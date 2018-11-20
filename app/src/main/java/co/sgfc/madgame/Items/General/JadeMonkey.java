package co.sgfc.madgame.Items.General;

import co.sgfc.madgame.Equipment;

public class JadeMonkey extends Equipment {
    public static final String ID = "JADE_MONKEY";

    public String getID() {
        return ID;
    }

    public JadeMonkey() {
        super("Jade Monkey", 5, 0.50);
    }

    @Override
    public boolean isUsable() {
        return false;
    }
}
