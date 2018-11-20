package co.sgfc.madgame.Database.CursorWrappers;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Point;

import co.sgfc.madgame.Database.Schema;
import co.sgfc.madgame.Item;
import co.sgfc.madgame.Items.ItemOwner;

import static co.sgfc.madgame.Database.Schema.itemFromId;

public class MapItemCursorWrapper extends CursorWrapper {
    public MapItemCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Item getItem() {
        long uniqueId = getLong(getColumnIndex(Schema.MapItems.Cols._ID));
        int x = getInt(getColumnIndex(Schema.MapItems.Cols.X));
        int y = getInt(getColumnIndex(Schema.MapItems.Cols.Y));
        String itemId = getString(getColumnIndex(Schema.MapItems.Cols.ITEM_ID));

        Item item = itemFromId(itemId);

        if (item != null) {
            item.setItemOwner(ItemOwner.MAP);
            item.setPosition(new Point(x, y));
            item.setUniqueId(uniqueId);
        }

        return item;
    }
}
