package co.sgfc.madgame.Database.CursorWrappers;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Point;

import co.sgfc.madgame.Database.Schema;
import co.sgfc.madgame.Item;
import co.sgfc.madgame.Items.ItemOwner;

import static co.sgfc.madgame.Database.Schema.itemFromId;

public class PlayerItemCursorWrapper extends CursorWrapper {
    public PlayerItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Item getItem() {
        long uniqueId = getLong(getColumnIndex(Schema.MapItems.Cols._ID));
        String itemId = getString(getColumnIndex(Schema.MapItems.Cols.ITEM_ID));

        Item item = itemFromId(itemId);

        if (item != null) {
            item.setItemOwner(ItemOwner.PLAYER);
            item.setPosition(null);
            item.setUniqueId(uniqueId);
        }

        return item;
    }
}
