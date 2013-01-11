package com.vokal.mapcraft.tileprovider;

import org.osmdroid.ResourceProxy.string;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;

public class OverviewerTileSource extends OnlineTileSourceBase {
    public OverviewerTileSource(final String aName, final string aResourceId,
        final int aZoomMinLevel, final int aZoomMaxLevel, final int aTileSizePixels,
        final String aImageFilenameEnding, final String... aBaseUrl) {

        super(aName, aResourceId, aZoomMinLevel, aZoomMaxLevel,
            aTileSizePixels, aImageFilenameEnding, aBaseUrl);
    }

    @Override
    public String getTileURLString(final MapTile aTile) {
            String url = getBaseUrl() + overviewer(aTile) + mImageFilenameEnding;
            android.util.Log.d(OverviewerTileSource.class.getSimpleName(), "URL: " + url);
            return url;
    }

    private String overviewer(final MapTile aTile) {
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
}
