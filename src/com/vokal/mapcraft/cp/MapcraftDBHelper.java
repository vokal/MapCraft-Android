package com.vokal.mapcraft.cp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.vokal.mapcraft.models.*;

public class MapcraftDBHelper extends SQLiteOpenHelper {

    private static final String TAG = MapcraftDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "mapcraft.db";
    private static int DATABASE_VERSION       = 5;

    // TODO:  we're going to need separate tables for the differnt server types probably
    // since it's impossible to have derived classes use a single base table
    public static final String TABLE_SERVER      = "server";
    public static final String TABLE_TILESET     = "tileset";
    public static final String TABLE_MARKERS     = "markers";

    MapcraftDBHelper(Context aContext) {
        super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase aDb) {
        aDb.execSQL(
            "CREATE TABLE " + TABLE_SERVER + " (" +
                    Server.ID   + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Server.URL  + " VARCHAR UNIQUE," +
                    Server.NAME + " VARCHAR" +
                    ");"
                );

        aDb.execSQL(
            "CREATE TABLE " + TABLE_TILESET + " (" +
                    TileSet.ID           + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TileSet.SERVER_URL   + " VARCHAR," +
                    TileSet.WORLD_NAME   + " VARCHAR," +
                    TileSet.NAME         + " VARCHAR," +
                    TileSet.BASE         + " VARCHAR," +
                    TileSet.PATH         + " VARCHAR," +
                    TileSet.EXT          + " VARCHAR," +
                    TileSet.BG_COLOR     + " VARCHAR," +
                    TileSet.MIN_ZOOM     + " INTEGER," +
                    TileSet.MAX_ZOOM     + " INTEGER," +
                    TileSet.DEFAULT_ZOOM + " INTEGER," +
                    TileSet.TILE_SIZE    + " INTEGER," +
                    OverviewerTileSet.NORTH_DIR + " INTEGER, " +
                    " UNIQUE (" + TileSet.SERVER_URL + ", " + TileSet.WORLD_NAME + ", " + TileSet.NAME + ")" +
                    ");"
                );

        aDb.execSQL(
            "CREATE TABLE " + TABLE_MARKERS + " (" +
                    OverviewerMarker.ID          + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    OverviewerMarker.WORLD       + " VARCHAR, " +
                    OverviewerMarker.GROUP       + " VARCHAR, " +
                    OverviewerMarker.ICON        + " VARCHAR, " +
                    OverviewerMarker.X           + " INTEGER, " +
                    OverviewerMarker.Y           + " INTEGER, " +
                    OverviewerMarker.Z           + " INTEGER"  +
                    ");"
                );


    }

    @Override
    public void onUpgrade(SQLiteDatabase aDb, int aOldVersion, int aNewVersion) {
        Log.w(TAG, "Upgrading database from " + aOldVersion + " to " + aNewVersion);

        aDb.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER);
        aDb.execSQL("DROP TABLE IF EXISTS " + TABLE_TILESET);
        aDb.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKERS);

        onCreate(aDb);
    }
}
