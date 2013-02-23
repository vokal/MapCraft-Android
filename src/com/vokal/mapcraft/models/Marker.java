package com.vokal.mapcraft.models;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;

import com.vokal.mapcraft.cp.MapcraftContentProvider;
import com.vokal.mapcraft.cp.MapcraftDBHelper;

public class Marker {

    public static final String TAG = Marker.class.getSimpleName();

    public static final String ID           = "_id";
    public static final String WORLD        = "world";
    public static final String GROUP        = "marker_group";
    public static final String ICON         = "icon";
    public static final String TEXT         = "text";
    public static final String X            = "x";
    public static final String Y            = "y";
    public static final String Z            = "z";
    public static final String[] ALL        = new String[] {
        ID, WORLD, GROUP, ICON, TEXT, X, Y, Z
    };

    public static final Uri CONTENT_URI = Uri.parse("content://" +
            MapcraftContentProvider.AUTHORITY + "/" + MapcraftDBHelper.TABLE_MARKERS);

    public int mId = -1;
    public String mWorld;
    public String mGroup;
    public String mIcon;
    public String mText;
    public int mX, mY, mZ;


    Marker() { }

    Marker(String aWorld, String aGroup, String aText, String aIcon, int aX, int aY, int aZ) {
        mWorld = aWorld;
        mGroup = aGroup;
        mText = aText;
        mIcon = aIcon;
        mX = aX;
        mY = aY;
        mZ = aZ;
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

        values.put(WORLD, mWorld);
        values.put(GROUP, mGroup);
        values.put(ICON, mIcon);
        values.put(TEXT, mText);
        values.put(X, mX);
        values.put(Y, mY);
        values.put(Z, mZ);

        return values;
    }

    public static Marker fromCursor(Cursor aCursor) {
        Marker result = null;

        if (MapcraftContentProvider.isValidCursor(aCursor)) {
            result = new Marker();

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
            mWorld = aCursor.getString(index);
        } else if (aColumn.equals(GROUP)) {
            mGroup = aCursor.getString(index);
        } else if (aColumn.equals(ICON)) {
            mIcon = aCursor.getString(index);
        } else if (aColumn.equals(TEXT)) {
            mText = aCursor.getString(index);
        } else if (aColumn.equals(X)) {
            mX = aCursor.getInt(index);
        } else if (aColumn.equals(Y)) {
            mY = aCursor.getInt(index);
        } else if (aColumn.equals(Z)) {
            mZ = aCursor.getInt(index);
        }
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d, %d, %d) %s", mWorld, mGroup, mX, mY, mZ, mText);
    }
}
