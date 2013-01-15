package com.vokal.mapcraft.models;

import android.content.*;
import android.database.*;
import android.net.Uri;

import org.json.*;
import org.osmdroid.tileprovider.MapTile;

import com.vokal.mapcraft.cp.MapcraftContentProvider;
import com.vokal.mapcraft.cp.MapcraftDBHelper;

public abstract class TileSet {

    public static final String ID           = "_id";
    public static final String SERVER_URL   = "server_key";
    public static final String WORLD_NAME   = "world_name";
    public static final String NAME         = "tileset_name";
    public static final String BASE         = "tileset_baseurl";
    public static final String PATH         = "tileset_urlpath";
    public static final String EXT          = "tileset_url_ext";
    public static final String BG_COLOR     = "tileset_bgcolor";
    public static final String MIN_ZOOM     = "tileset_min_zoom";
    public static final String MAX_ZOOM     = "tileset_max_zoom";
    public static final String DEFAULT_ZOOM = "tileset_default_zoom";
    public static final String[] ALL        = new String[] {
        ID, SERVER_URL, WORLD_NAME, NAME, BASE, PATH, EXT, BG_COLOR,
        MIN_ZOOM, MAX_ZOOM, DEFAULT_ZOOM
    };

    public static final Uri CONTENT_URI = Uri.parse("content://" +
        MapcraftContentProvider.AUTHORITY + "/" + MapcraftDBHelper.TABLE_TILESET);

    private int mId = -1;
    private String mServerUrl;
    private String mWorldName;
    private String mName;
    private String mBase;
    private String mPath;
    private String mExt;
    private int mBgColor;
    private int mMinZoom;
    private int mMaxZoom;
    private int mDefaultZoom;

    TileSet() {
    }

    TileSet(JSONObject aObj) throws Exception {
        mWorldName = aObj.getString("world");
        mName      = aObj.getString("name");
        mPath      = aObj.getString("path");
        mExt       = aObj.getString("imgextension");
    }

    public String getServerUrl() {
        return mServerUrl;
    }
    
    public void setServerUrl(final String aServerUrl) {
        mServerUrl = aServerUrl;
    }

    public String getWorldName() {
        return mWorldName;
    }

    public String getName() {
        return mName;
    }

    public String getBaseUrl() {
        if (mBase != null) {
            return mBase + "/" + mPath;
        }
        return mServerUrl + "/" + mPath;
    }

    public String getImageExt() {
        return mExt;
    }

    public abstract String getPathForTile(final MapTile aTile);

    public int getMinZoom() {
        return mMinZoom;
    }

    public int getMaxZoom() {
        return mMaxZoom;
    }

    public int getDefaultZoom() {
        return mDefaultZoom;
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

        values.put(SERVER_URL, mServerUrl);
        values.put(WORLD_NAME, mWorldName);
        values.put(NAME, mName);
        values.put(BASE, mBase);
        values.put(PATH, mPath);
        values.put(EXT, mExt);

        return values;
    }

    public static TileSet fromCursor(Cursor aCursor) {
        TileSet result = null;

        if (MapcraftContentProvider.isValidCursor(aCursor)) {
            result = new OverviewerTileSet();

            int index = 0;
            for(String name : aCursor.getColumnNames()) {
                result.setByCursorColumn(aCursor, name, index);
                ++index;
            }
        }

        return result;
    }

    private void setByCursorColumn(final Cursor aCursor, final String aName, final int index) {
        if (aName.equals(ID)) {
            mId = aCursor.getInt(index);
        } else if (aName.equals(SERVER_URL)) {
            mServerUrl = aCursor.getString(index);
        } else if (aName.equals(WORLD_NAME)) {
            mWorldName = aCursor.getString(index);
        } else if (aName.equals(NAME)) {
            mName = aCursor.getString(index);
        } else if (aName.equals(PATH)) {
            mPath = aCursor.getString(index);
        } else if (aName.equals(EXT)) {
            mExt = aCursor.getString(index);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(TileSet.class.getSimpleName()).append("\n");
        builder.append("    ").append("World Name: ").append(mWorldName).append("\n");
        builder.append("    ").append("Name:       ").append(mName).append("\n");
        builder.append("    ").append("Path:       ").append(mPath).append("\n");
        builder.append("    ").append("Ext:        ").append(mExt).append("\n");
        return builder.toString();
    }
}
