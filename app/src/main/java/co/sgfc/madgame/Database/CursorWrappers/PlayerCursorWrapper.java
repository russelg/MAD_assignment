package co.sgfc.madgame.Database.CursorWrappers;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Point;

import co.sgfc.madgame.Area;
import co.sgfc.madgame.Database.Schema;
import co.sgfc.madgame.Player;

public class PlayerCursorWrapper extends CursorWrapper {
    public PlayerCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Player getPlayer() {
        long uniqueId = getLong(getColumnIndex(Schema.Player.Cols._ID));
        int x = getInt(getColumnIndex(Schema.Player.Cols.X));
        int y = getInt(getColumnIndex(Schema.Player.Cols.Y));
        double cash = getDouble(getColumnIndex(Schema.Player.Cols.CASH));
        double health = getDouble(getColumnIndex(Schema.Player.Cols.HEALTH));

        Player player = new Player();
        player.setUniqueId(uniqueId);
        player.setLocation(new Point(x, y));
        player.setCash(cash);
        player.setHealth(health);

        return player;
    }
}
