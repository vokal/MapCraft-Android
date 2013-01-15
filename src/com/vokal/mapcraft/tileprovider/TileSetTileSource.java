package com.vokal.mapcraft.tileprovider;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

import com.vokal.mapcraft.models.TileSet;

public class TileSetTileSource extends OnlineTileSourceBase {
    TileSet mTileSet;
    public TileSetTileSource(final TileSet aTileSet, final int aTileSizePixels) {
        super(aTileSet.getName(), null, aTileSet.getMinZoom(), aTileSet.getMaxZoom(),
            aTileSizePixels, aTileSet.getImageExt(), aTileSet.getBaseUrl());
        mTileSet = aTileSet;

    }

    @Override
    public String getTileURLString(final MapTile aTile) {
        return mTileSet.getBaseUrl() + mTileSet.getPathForTile(aTile) + mTileSet.getImageExt();
    }
}
