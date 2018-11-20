package co.sgfc.madgame.Database.CursorWrappers;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Point;

import co.sgfc.madgame.Area;
import co.sgfc.madgame.Database.Schema;

public class MapCursorWrapper extends CursorWrapper {
    public MapCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Area getArea() {
        long uniqueId = getLong(getColumnIndex(Schema.Map.Cols._ID));
        int x = getInt(getColumnIndex(Schema.Map.Cols.X));
        int y = getInt(getColumnIndex(Schema.Map.Cols.Y));
        String description = getString(getColumnIndex(Schema.Map.Cols.DESCRIPTION));
        boolean town = getInt(getColumnIndex(Schema.Map.Cols.TOWN)) != 0;
        boolean starred = getInt(getColumnIndex(Schema.Map.Cols.STARRED)) != 0;
        boolean explored = getInt(getColumnIndex(Schema.Map.Cols.EXPLORED)) != 0;

        Area area = new Area(town);
        area.setUniqueId(uniqueId);
        area.setPosition(new Point(x, y));
        area.setDescription(description);
        area.setStarred(starred);
        area.setExplored(explored);

        return area;
    }
}
