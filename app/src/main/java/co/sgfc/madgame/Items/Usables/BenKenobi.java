package co.sgfc.madgame.Items.Usables;

import java.util.ArrayList;

import co.sgfc.madgame.Area;
import co.sgfc.madgame.Equipment;
import co.sgfc.madgame.Food;
import co.sgfc.madgame.GameData;
import co.sgfc.madgame.Item;
import co.sgfc.madgame.Player;

public class BenKenobi extends Equipment {
    public static final String ID = "BEN_KENOBI";

    public String getID() {
        return ID;
    }

    public BenKenobi() {
        super("Ben Kenobi", 10, 0.0);
    }

    @Override
    public boolean isUsable() {
        return true;
    }

    @Override
    public void use() {
        GameData gameData = GameData.getInstance();
        Player player = gameData.getPlayer();
        Area area = gameData.getPlayerArea();

        for (Item item : new ArrayList<>(area.getItems())) {
            if (item instanceof Food) {
                item.use();
            } else {
                player.addItem(item);
            }
            area.removeItem(item);
        }

        gameData.saveData();
    }
}
