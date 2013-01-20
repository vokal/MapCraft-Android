package com.vokal.mapcraft.models;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;

import org.json.JSONObject;

import com.vokal.mapcraft.cp.MapcraftContentProvider;
import com.vokal.mapcraft.cp.MapcraftDBHelper;

public class OverviewerMarker {

    public static final String TAG = OverviewerMarker.class.getSimpleName();

    public static final String ID           = "_id";
    public static final String WORLD        = "world";
    public static final String GROUP        = "marker_group";
    public static final String ICON         = "icon";
    public static final String X            = "x";
    public static final String Y            = "y";
    public static final String Z            = "z";


    public static final String[] ALL        = new String[] {
        ID, WORLD, GROUP, ICON, X, Y, Z
    };

    public static final Uri CONTENT_URI = Uri.parse("content://" +
            MapcraftContentProvider.AUTHORITY + "/" + MapcraftDBHelper.TABLE_MARKERS);

    public static class MarkerGroup {
        String world;
        String displayName;
        String groupName;
        String icon;
    }

    public int mId = -1;
    public MarkerGroup mGroup = new MarkerGroup();
    public String icon;
    public int x, y, z;


    OverviewerMarker() { }

    OverviewerMarker(MarkerGroup aGroup, JSONObject markerJSON) throws Exception {
        mGroup       = aGroup;

    }

    public void save(Context aContext) {
        ContentValues values = getContentValues();

        Uri inserted = aContext.getContentResolver().insert(CONTENT_URI, values);
        try {
            mId = (int) ContentUris.parseId(inserted);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void saveBulk(final Context aContext, ContentValues[] aValues) {
        aContext.getContentResolver().bulkInsert(CONTENT_URI, aValues);
    }

    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        if (mId != -1) {
            values.put(ID, mId);
        }

        values.put(WORLD, mGroup.world);
        values.put(GROUP, mGroup.displayName);
        values.put(ICON, icon);
        values.put(X, x);
        values.put(Y, y);
        values.put(Z, z);

        return values;
    }

    public static OverviewerMarker fromCursor(Cursor aCursor) {
        OverviewerMarker result = null;

        if (MapcraftContentProvider.isValidCursor(aCursor)) {
            result = new OverviewerMarker();

            int index = 0;
            for(String name : aCursor.getColumnNames()) {
                result.setByCursorColumn(aCursor, name, index);
                ++index;
            }
        }

        return result;
    }

    private void setByCursorColumn(final Cursor aCursor, final String aColumn, final int index) {
        if (aColumn.equals(ID)) {
            mId = aCursor.getInt(index);
        } else if (aColumn.equals(WORLD)) {
            mGroup.world = aCursor.getString(index);
        } else if (aColumn.equals(GROUP)) {
            mGroup.displayName = aCursor.getString(index);
        } else if (aColumn.equals(ICON)) {
            icon = aCursor.getString(index);
        } else if (aColumn.equals(X)) {
            x = aCursor.getInt(index);
        } else if (aColumn.equals(Y)) {
            y = aCursor.getInt(index);
        } else if (aColumn.equals(Z)) {
            z = aCursor.getInt(index);
        }
    }

//    @Override
//    public boolean equals(Object aMarkerSet) {
//        if (aMarkerSet == null || !(aMarkerSet instanceof OverviewerMarker) ) {
//            return false;
//        }
//
//        if (aMarkerSet == this) {
//            return true;
//        }
//
//        OverviewerMarker ms = (OverviewerMarker) aMarkerSet;
//
//        return false;
//        return mWorld.equals(ms.mWorld) && mGroup.equals(ms.mGroup) && mName.equals(ms.mName);
//    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(TAG).append("\n");
        builder.append("    ").append("World:      ").append(mGroup.world).append("\n");
        builder.append("    ").append("Name:       ").append(mGroup.displayName).append("\n");
        builder.append("    ").append("Icon:       ").append(icon).append("\n");
        builder.append("    ").append("Position:   ").append(String.format("(%d, %d, %d)", x,y,z));
        return builder.toString();
    }
}
