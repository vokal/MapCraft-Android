package com.vokal.mapcraft.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONObject;
import org.osmdroid.tileprovider.MapTile;

public class OverviewerTileSet extends TileSet {

    public static final String TAG = "OverviewerTileSet";

    OverviewerTileSet() { }

    public OverviewerTileSet(JSONObject aObj) throws Exception {
         super(aObj);
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

    public String getPreviewTile() {
        return getBaseUrl() + "/base" + getImageExt();
    }
}
