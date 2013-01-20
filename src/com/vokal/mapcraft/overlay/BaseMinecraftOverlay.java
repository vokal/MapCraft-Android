package com.vokal.mapcraft.overlay;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;

import org.osmdroid.api.IMapView;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import com.vokal.mapcraft.models.TileSet;

public abstract class BaseMinecraftOverlay extends ItemizedOverlay<OverlayItem> {

    public static final String TAG = BaseMinecraftOverlay.class.getSimpleName();

    protected String mName;
    protected String mRenderSet;  // TODO:  is this specific to Overviewer?
    protected ArrayList<String> mTileSets;

    protected int tileSize;
    protected int zoomLevels;
    protected int mapSize;
    protected int centerPixel;
    protected int numTiles;

    public BaseMinecraftOverlay(Drawable aDefaultMarker, MapView aMapView, TileSet aTileSet) {
        super(aDefaultMarker, aMapView.getResourceProxy());

        mName       = aTileSet.getName();
        mRenderSet  = aTileSet.getRenderSet();

        tileSize = aMapView.getTileProvider().getTileSource().getTileSizePixels();
        int maxZoom = aMapView.getTileProvider().getMaximumZoomLevel();
        int minZoom = aMapView.getTileProvider().getMinimumZoomLevel();
        zoomLevels = maxZoom - minZoom;
        if (minZoom == 0) {
            zoomLevels++;
        }
        mapSize = tileSize << zoomLevels;
        centerPixel = mapSize / 2;
        numTiles = mapSize / tileSize;

        Log.d(TAG, "tileSize: " + tileSize);
        Log.d(TAG, "zoomLevels: " + zoomLevels);
        Log.d(TAG, "mapSize: " + mapSize);
        Log.d(TAG, "numTiles: " + numTiles);
    }

    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point aPoint, IMapView aMapView) {
        Log.d(TAG, String.format("onSnapToItem(%d, %d, %s)", arg0, arg1, aPoint.toString()));
        return false;
    }

    @Override
    abstract protected OverlayItem createItem(int aIndex);

    @Override
    abstract public int size();



}
