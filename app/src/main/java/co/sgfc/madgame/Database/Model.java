package co.sgfc.madgame.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import co.sgfc.madgame.Area;
import co.sgfc.madgame.Database.CursorWrappers.*;
import co.sgfc.madgame.Item;
import co.sgfc.madgame.Player;

public class Model {
    private SQLiteDatabase db;
    private Context context;

    public Model(Context context) {
        this.context = context.getApplicationContext();
        this.db = new DbHelper(this.context).getWritableDatabase();
    }

    private MapCursorWrapper queryMapCells(String whereClause, String[] whereArgs) {
        Cursor cursor = db.query(
                Schema.Map.NAME, null, whereClause, whereArgs,
                null, null, null);

        return new MapCursorWrapper(cursor);
    }

    public Area[][] getMap(int rows, int cols) {
        Area[][] grid = new Area[rows + 1][cols + 1];
        List<Area> areas = new ArrayList<>();

        try (MapCursorWrapper cursor = queryMapCells(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Area area = cursor.getArea();
                area.setItems(getMapItems(area));
                areas.add(area);
                cursor.moveToNext();
            }
        }

        if (areas.size() == ((rows + 1) * (cols + 1))) {
            for (Area area : areas) {
                Point pos = area.getPosition();
                grid[pos.x][pos.y] = area;
            }
            return grid;
        } else {
            return null;
        }
    }

    public void removeMap() {
        db.delete(Schema.Map.NAME, null, null);
        removeMapItems();
    }

    public void addMapItems(Area area) {
        removeMapItems(area);
        for (Item item : area.getItems()) {
            addMapItem(item);
        }
    }

    public void addMapCell(Area area) {
        ContentValues cv = Schema.Map.getContentValues(area);
        long uniqueId = db.insert(Schema.Map.NAME, null, cv);
        area.setUniqueId(uniqueId);
        addMapItems(area);
    }

    public void updateMapCell(Area area) {
        ContentValues cv = Schema.Map.getContentValues(area);
        String[] args = new String[]{String.valueOf(area.getUniqueId())};
        db.update(Schema.Map.NAME, cv, Schema.Map.Cols._ID + " =  ?", args);
        addMapItems(area);
    }

    public void removeMapItems() {
        removeMapItems(null);
    }

    public void removeMapItems(Area area) {
        String where;
        if (area != null) {
            Point pos = area.getPosition();
            where = String.format("%s = %s AND %s = %s", Schema.MapItems.Cols.X, pos.x, Schema.MapItems.Cols.Y, pos.y);
        } else {
            where = null;
        }
        db.delete(Schema.MapItems.NAME, where, null);
    }

    private MapItemCursorWrapper queryMapItems(String whereClause, String[] whereArgs) {
        Cursor cursor = db.query(
                Schema.MapItems.NAME, null, whereClause, whereArgs,
                null, null, null);

        return new MapItemCursorWrapper(cursor);
    }

    public List<Item> getMapItems(Area area) {
        // get all items at given position
        List<Item> items = new ArrayList<>();
        String where = null;

        if (area != null) {
            Point pos = area.getPosition();
            where = String.format("%s = %s AND %s = %s", Schema.MapItems.Cols.X, pos.x, Schema.MapItems.Cols.Y, pos.y);
        }

        try (MapItemCursorWrapper cursor = queryMapItems(where, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getItem());
                cursor.moveToNext();
            }
        }

        return items;
    }

    public void addMapItem(Item item) {
        ContentValues cv = Schema.MapItems.getContentValues(item);
        long uniqueId = db.insert(Schema.MapItems.NAME, null, cv);
        item.setUniqueId(uniqueId);
    }

    private PlayerItemCursorWrapper queryPlayerItems(String whereClause, String[] whereArgs) {
        Cursor cursor = db.query(
                Schema.PlayerItems.NAME, null, whereClause, whereArgs,
                null, null, null);

        return new PlayerItemCursorWrapper(cursor);
    }

    public List<Item> getPlayerItems() {
        // get all items at given position
        List<Item> items = new ArrayList<>();

        try (PlayerItemCursorWrapper cursor = queryPlayerItems(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                items.add(cursor.getItem());
                cursor.moveToNext();
            }
        }

        return items;
    }

    public void addPlayerItem(Item item) {
        ContentValues cv = Schema.PlayerItems.getContentValues(item);
        long uniqueId = db.insert(Schema.PlayerItems.NAME, null, cv);
        item.setUniqueId(uniqueId);
    }

    public void removePlayerItems() {
        db.delete(Schema.PlayerItems.NAME, null, null);
    }

    private void addPlayerItems(Player player) {
        removePlayerItems();
        for (Item item : player.getItems()) {
            addPlayerItem(item);
        }
    }

    private PlayerCursorWrapper queryPlayer(String whereClause, String[] whereArgs) {
        Cursor cursor = db.query(
                Schema.Player.NAME, null, whereClause, whereArgs,
                null, null, null);

        return new PlayerCursorWrapper(cursor);
    }

    public Player getPlayer() {
        Player player;

        try (PlayerCursorWrapper cursor = queryPlayer(null, null)) {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            player = cursor.getPlayer();
        }

        List<Item> playerItems = getPlayerItems();
        player.setItems(playerItems);

        return player;
    }

    public void addPlayer(Player player) {
        ContentValues cv = Schema.Player.getContentValues(player);
        db.insert(Schema.Player.NAME, null, cv);
        addPlayerItems(player);
    }

    public void updatePlayer(Player player) {
        ContentValues cv = Schema.Player.getContentValues(player);
        db.update(Schema.Player.NAME, cv, null, null);
        addPlayerItems(player);
    }

    public void removePlayer() {
        db.delete(Schema.Player.NAME, null, null);
        removePlayerItems();
    }
}
