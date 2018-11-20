package co.sgfc.madgame.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import co.sgfc.madgame.Database.Schema.*;

public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "game.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s, %s, %s, %s, %s, %s)",
                Map.NAME, Map.Cols._ID, Map.Cols.X, Map.Cols.Y, Map.Cols.DESCRIPTION, Map.Cols.TOWN, Map.Cols.STARRED, Map.Cols.EXPLORED));
        db.execSQL(String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s, %s, %s)",
                MapItems.NAME, MapItems.Cols._ID, MapItems.Cols.ITEM_ID, Map.Cols.X, Map.Cols.Y));
        db.execSQL(String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s)",
                PlayerItems.NAME, PlayerItems.Cols._ID, PlayerItems.Cols.ITEM_ID));
        db.execSQL(String.format("create table %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s, %s, %s, %s)",
                Player.NAME, Player.Cols._ID, Player.Cols.X, Player.Cols.Y, Player.Cols.CASH, Player.Cols.HEALTH));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int v1, int v2) {

    }
}
