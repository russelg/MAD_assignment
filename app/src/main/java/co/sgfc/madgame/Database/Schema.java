package co.sgfc.madgame.Database;

import android.content.ContentValues;
import android.graphics.Point;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import co.sgfc.madgame.Area;
import co.sgfc.madgame.GameData;
import co.sgfc.madgame.Item;
import co.sgfc.madgame.Items.General.*;
import co.sgfc.madgame.Items.General.Food.Apple;
import co.sgfc.madgame.Items.Usables.*;

public class Schema {
    public static Item itemFromId(String itemId) {
        Item item = null;

        List<Class<? extends Item>> items = new ArrayList<>(GameData.gameItems);
        items.addAll(GameData.winningItems);

        for (Class<? extends Item> itemCls : items) {
            try {
                Item tmpItem = itemCls.newInstance();
                if (itemId.equals(tmpItem.getID())) {
                    item = tmpItem;
                }
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        return item;
    }

    public static class Map {
        public static final String NAME = "map";

        public static class Cols implements BaseColumns {
            public static final String X = "x";
            public static final String Y = "y";
            public static final String TOWN = "town";
            public static final String DESCRIPTION = "description";
            public static final String STARRED = "starred";
            public static final String EXPLORED = "explored";
        }

        public static ContentValues getContentValues(Area area) {
            ContentValues cv = new ContentValues();
            Point point = area.getPosition();
            cv.put(Cols.X, point.x);
            cv.put(Cols.Y, point.y);
            cv.put(Cols.TOWN, area.isTown());
            cv.put(Cols.DESCRIPTION, area.getDescription());
            cv.put(Cols.STARRED, area.isStarred());
            cv.put(Cols.EXPLORED, area.isExplored());
            return cv;
        }
    }

    public static class MapItems {
        public static final String NAME = "map_items";

        public static class Cols implements BaseColumns {
            public static final String ITEM_ID = "item_id";
            public static final String X = "x";
            public static final String Y = "y";
        }

        public static ContentValues getContentValues(Item item) {
            ContentValues cv = new ContentValues();
            Point point = item.getPosition();
            cv.put(Cols.ITEM_ID, item.getID());
            cv.put(Cols.X, point.x);
            cv.put(Cols.Y, point.y);
            return cv;
        }
    }

    public static class PlayerItems {
        public static final String NAME = "player_items";

        public static class Cols implements BaseColumns {
            public static final String ITEM_ID = "item_id";
        }

        public static ContentValues getContentValues(Item item) {
            ContentValues cv = new ContentValues();
            cv.put(Cols.ITEM_ID, item.getID());
            return cv;
        }
    }

    public static class Player {
        public static final String NAME = "player";

        public static class Cols implements BaseColumns {
            public static final String X = "x";
            public static final String Y = "y";
            public static final String CASH = "cash";
            public static final String HEALTH = "health";
        }

        public static ContentValues getContentValues(co.sgfc.madgame.Player player) {
            ContentValues cv = new ContentValues();
            Point pos = player.getLocation();
            cv.put(Cols.X, pos.x);
            cv.put(Cols.Y, pos.y);
            cv.put(Cols.CASH, player.getCash());
            cv.put(Cols.HEALTH, player.getHealth());
            return cv;
        }
    }
}
