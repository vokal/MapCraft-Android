package com.vokal.mapcraft.models;

import android.database.*;
import android.content.Context;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.content.Loader.ForceLoadContentObserver;
import android.widget.SpinnerAdapter;

public class WorldTilesetNavLoader extends AsyncTaskLoader<SpinnerAdapter> {
    final ContentObserver mObserver;
    String mSelection;
    Server mServer;
    Context mContext;

    public WorldTilesetNavLoader(final Context aContext, final Server aServer) {
        super(aContext);
        mContext = aContext;
        mServer = aServer;

        mObserver = new ForceLoadContentObserver();
    }
    
    public WorldTilesetNavLoader(final Context aContext, final Server aServer, final String aSelection) {
        super(aContext);

        mContext = aContext;
        mServer = aServer;
        mSelection = aSelection;

        mObserver = new ForceLoadContentObserver();
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }

    public SpinnerAdapter loadInBackground() {
        String[] projection = new String[] {"DISTINCT " + TileSet.WORLD_NAME};
        Cursor c = mContext.getContentResolver().query(TileSet.CONTENT_URI, projection, 
            TileSet.SERVER_URL + " = '" + mServer.getUrl() + "'"
            , null, null);

        mContext.getContentResolver().registerContentObserver(TileSet.CONTENT_URI, true, mObserver);

        while (c.moveToNext()) {
            System.out.println("WORLD: " + c.getString(0));
        }

        c.close();

        return null;
    }
}
