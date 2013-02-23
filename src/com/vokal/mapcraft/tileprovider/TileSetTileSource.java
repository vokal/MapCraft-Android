package com.vokal.mapcraft.tileprovider;

import android.util.Log;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

import com.vokal.mapcraft.models.TileSet;

public class TileSetTileSource extends OnlineTileSourceBase {

    public static final String TAG = TileSetTileSource.class.getSimpleName();

    TileSet mTileSet;

    public TileSetTileSource(final TileSet aTileSet) {
        super(aTileSet.getName(), null, aTileSet.getMinZoom(), aTileSet.getMaxZoom(),
            aTileSet.getTileSize(), aTileSet.getImageExt(), aTileSet.getBaseUrl());
        mTileSet = aTileSet;
    }

    @Override
    public String getTileURLString(final MapTile aTile) {
        String url = mTileSet.getBaseUrl() + mTileSet.getPathForTile(aTile) + mTileSet.getImageExt();
        Log.d(TAG, url);
        return url;
    }

    @Override
    public String toString() {
        return mTileSet.toString();
    }
}
