package com.vokal.mapcraft.models;

import android.database.*;
import android.content.Context;
import android.os.*;
import android.support.v4.content.*;
import android.support.v4.content.Loader.ForceLoadContentObserver;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;

import com.vokal.mapcraft.models.TileSet;
import com.vokal.mapcraft.widget.TileSetNavAdapter;

public class WorldTilesetNavLoader extends AsyncTaskLoader<SpinnerAdapter> {
    final ContentObserver mObserver;
    TileSet mSelection;
    Server mServer;
    Context mContext;

    public WorldTilesetNavLoader(final Context aContext, final Server aServer) {
        super(aContext);
        mContext = aContext;
        mServer = aServer;

        mObserver = new ForceLoadContentObserver();
    }
    
    public WorldTilesetNavLoader(final Context aContext, final Server aServer, final TileSet aSelection) {
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
        Cursor c = mContext.getContentResolver().query(TileSet.CONTENT_URI, TileSet.ALL, 
            TileSet.SERVER_URL + " = '" + mServer.getUrl() + "'"
            , null, TileSet.ID + " ASC");

        mContext.getContentResolver().registerContentObserver(TileSet.CONTENT_URI, true, mObserver);
        
        String lastWorld = null;
        ArrayList<TileSet> worldDefaults = new ArrayList<TileSet>();
        ArrayList<TileSet> selected = new ArrayList<TileSet>();

        while (c.moveToNext()) {
            TileSet t = TileSet.fromCursor(c);
            if (mSelection == null) {
                mSelection = t;
            }

            if (!t.getWorldName().equals(lastWorld)) {
                lastWorld = t.getWorldName();
                worldDefaults.add(t);
            }

            if (t.getWorldName().equals(mSelection.getWorldName())) {
                selected.add(t);
            }
        }

        int separator = worldDefaults.size();
        selected.addAll(0, worldDefaults);

        c.close();

        return new TileSetNavAdapter(mContext, selected, separator);
    }
}
