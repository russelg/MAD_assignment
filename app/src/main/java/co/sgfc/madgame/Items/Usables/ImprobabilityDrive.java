package co.sgfc.madgame.Items.Usables;

import co.sgfc.madgame.Equipment;
import co.sgfc.madgame.GameData;

public class ImprobabilityDrive extends Equipment {
    public static final String ID = "IMPROBABILITY_DRIVE";

    public String getID() {
        return ID;
    }

    public ImprobabilityDrive() {
        super("Improbability Drive", 10, -Math.PI);
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void use() {
        GameData gameData = GameData.getInstance();
        gameData.initializeGameMap();
        gameData.getPlayerArea().setExplored(true);
        gameData.saveGameMap();
    }
}
