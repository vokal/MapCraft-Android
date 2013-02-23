package com.vokal.mapcraft.cp;

import android.net.Uri;

public class MapcraftContentProvider extends SQLiteSimpleContentProvider {

    public static final String AUTHORITY = "com.vokal.mapcraft.cp";

    private static final int SERVER  = 0;
    private static final int TILESET = 1;
    private static final int MARKERS = 2;

    @Override
    public boolean onCreate() {
        mDbHelper = new MapcraftDBHelper(getContext());

        return super.onCreate();
    }

    @Override
    protected String getTableFromUri(final Uri aUri) {
        final int match = URI_MATCHER.match(aUri);
        switch(match) {
            case SERVER:
                return MapcraftDBHelper.TABLE_SERVER;
            case TILESET:
                return MapcraftDBHelper.TABLE_TILESET;
            case MARKERS:
                return MapcraftDBHelper.TABLE_MARKERS;
        }

        return null;
    }

    static {
        URI_MATCHER.addURI(AUTHORITY, MapcraftDBHelper.TABLE_SERVER, SERVER);
        URI_MATCHER.addURI(AUTHORITY, MapcraftDBHelper.TABLE_TILESET, TILESET);
        URI_MATCHER.addURI(AUTHORITY, MapcraftDBHelper.TABLE_MARKERS, MARKERS);
    }
}
