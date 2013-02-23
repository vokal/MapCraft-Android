package com.vokal.mapcraft.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;
import org.osmdroid.tileprovider.MapTile;

public class OverviewerTileSet extends TileSet {

    public static final String TAG = "OverviewerTileSet";

    public static final String NORTH_DIR    = "tileset_north_direction";

    public static final String[] ALL        = new String[] {
        ID, SERVER_URL, WORLD_NAME, NAME, BASE, PATH, EXT, BG_COLOR,
        MIN_ZOOM, MAX_ZOOM, DEFAULT_ZOOM, TILE_SIZE, NORTH_DIR
    };

    public enum NorthDirection {
        UPPER_LEFT, UPPER_RIGHT, LOWER_RIGHT, LOWER_LEFT
    }

    private NorthDirection mNorthDirection = NorthDirection.UPPER_LEFT;

    OverviewerTileSet() { }

    public OverviewerTileSet(JSONObject aObj) throws Exception {
        super(aObj);
        mNorthDirection = NorthDirection.values()[aObj.getInt("north_direction")];
        Log.d(TAG, "Constructor: " + this.getName());
    }

    public String getPathForTile(final MapTile aTile) {
        Log.d(TAG, aTile.toString());
        StringBuilder builder = new StringBuilder();
        if (aTile.getX() >= Math.pow(2.0, aTile.getZoomLevel()) ||
                aTile.getY() >= Math.pow(2.0, aTile.getZoomLevel())) {
            builder.append("/blank");
        } else if (aTile.getZoomLevel() == 0) {
            builder.append("/base");
        } else {
            for (int i = aTile.getZoomLevel() - 1; i >= 0; --i) {
                int xInside = (int) Math.floor(aTile.getX() / Math.pow(2.0, i)) % 2;
                int yInside = (int) Math.floor(aTile.getY() / Math.pow(2.0, i)) % 2;
                builder.append("/");
                builder.append(xInside + 2 * yInside);
            }
        }
        return builder.append(".").toString();
    }

    @Override
    protected ContentValues getContentValues() {
        ContentValues values =  super.getContentValues();
        values.put(NORTH_DIR, mNorthDirection.ordinal());
        return values;
    }

    @Override
    protected void setByCursorColumn(Cursor aCursor, String aName, int index) {
        if (aName.equals(NORTH_DIR)) {
            mNorthDirection = NorthDirection.values()[aCursor.getInt(index)];
        } else {
            super.setByCursorColumn(aCursor, aName, index);
        }
    }

    public String getPreviewTile() {
        return getBaseUrl() + "/base" + getImageExt();
    }

    public NorthDirection getNorthDirection() {
        return mNorthDirection;
    }

    @Override
    public String toString() {
        String s = super.toString();
        return s.concat("    ").concat("North Dir:  ").concat(mNorthDirection.name()).concat("\n");
    }




}
