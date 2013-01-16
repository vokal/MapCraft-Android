package com.vokal.mapcraft.tileprovider;

import org.osmdroid.tileprovider.*;
import org.osmdroid.tileprovider.modules.*;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;

import android.content.Context;

import com.vokal.mapcraft.models.TileSet;

public class MapTileProviderMultiSource extends MapTileProviderArray implements IMapTileProviderCallback {

    public MapTileProviderMultiSource(final Context pContext, final ITileSource pTileSource) {
            this(new SimpleRegisterReceiver(pContext), new NetworkAvailabliltyCheck(pContext),
                            pTileSource);
    }

    public MapTileProviderMultiSource(final IRegisterReceiver pRegisterReceiver,
                    final INetworkAvailablityCheck aNetworkAvailablityCheck, final ITileSource pTileSource) {
            super(pTileSource, pRegisterReceiver);

            final TileWriter tileWriter = new TileWriter();

            final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(
                            pRegisterReceiver, pTileSource);
            mTileProviderList.add(fileSystemProvider);

            final MapTileFileArchiveProvider archiveProvider = new MapTileFileArchiveProvider(
                            pRegisterReceiver, pTileSource);
            mTileProviderList.add(archiveProvider);

            final MultiSourceTileDownloader downloaderProvider = new MultiSourceTileDownloader(pTileSource, tileWriter,
                            aNetworkAvailablityCheck);
            mTileProviderList.add(downloaderProvider);
    }
}
