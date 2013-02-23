package com.vokal.mapcraft.overlay;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.util.ArrayList;

import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import com.vokal.mapcraft.models.Marker;
import com.vokal.mapcraft.models.TileSet;

public abstract class BaseMinecraftOverlay extends ItemizedOverlay<OverlayItem>
implements LoaderCallbacks<ArrayList<Marker>>{

    public static final String      TAG = BaseMinecraftOverlay.class.getSimpleName();

    protected static int            sLoaderIndex = Integer.MAX_VALUE;

    protected String                mName;

    protected int                   tileSize;
    protected int                   zoomLevels;
    protected int                   mapSize;
    protected int                   centerPixel;
    protected int                   numTiles;

    protected ArrayList<Marker>     mMarkerData;

    protected Context               mContext;
    private   MapView               mMapView;

    public BaseMinecraftOverlay(
            FragmentActivity aContext,
            MapView aMapView,
            TileSet aTileSet,
            int aDefaultMarkerId) {

        super(aContext.getResources().getDrawable(aDefaultMarkerId), aMapView.getResourceProxy());

        mContext        = aContext;
        mMapView        = aMapView;

        mName           = aTileSet.getName();

        tileSize        = aMapView.getTileProvider().getTileSource().getTileSizePixels();
        int maxZoom     = aMapView.getTileProvider().getMaximumZoomLevel();
        int minZoom     = aMapView.getTileProvider().getMinimumZoomLevel();

        zoomLevels = maxZoom - minZoom;
        if (minZoom <= 0) {
            zoomLevels++;
        }

        mapSize = tileSize << zoomLevels;
        centerPixel = mapSize / 2;
        numTiles = mapSize / tileSize;

        Log.d(TAG, "tileSize: " + tileSize);
        Log.d(TAG, "zoomLevels: " + zoomLevels);
        Log.d(TAG, "mapSize: " + mapSize);
        Log.d(TAG, "numTiles: " + numTiles);

        sLoaderIndex--;
        aContext.getSupportLoaderManager().initLoader(sLoaderIndex, null, this);
    }

    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point aPoint, IMapView aMapView) {
//        Log.d(TAG, String.format("onSnapToItem(%d, %d, %s)", arg0, arg1, aPoint.toString()));
        return false;
    }

    @Override
    public int size() {
        return mMarkerData.size();
    }



    @Override
    public void onDetach(MapView mapView) {
        super.onDetach(mapView);
        mMarkerData.clear();
        refresh();
//        sLoaderIndex++;
    }

    @Override
    protected abstract OverlayItem createItem(int aIndex);

    protected abstract String getTitle(Marker aMarker);
    protected abstract String getDescription(Marker aMarker);
    protected abstract GeoPoint getGeoPoint(Marker aMarker);

//    protected abstract Loader<ArrayList<? extends Marker>> getLoader();

    protected abstract Cursor getCursor();

    @Override
    public Loader<ArrayList<Marker>> onCreateLoader(int aId, Bundle aArgs) {
        return new MarkerLoader(mContext, this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Marker>> aLoader, ArrayList<Marker> aData) {
        mMarkerData = aData;
        refresh();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Marker>> aLoader) {
        mMarkerData.clear();
        refresh();
    }

    private void refresh() {
        populate();
        mMapView.postInvalidate();
    }

    private static class MarkerLoader extends AsyncTaskLoader<ArrayList<Marker>> {

        private final Context               mContext;
        private final ContentObserver       mObserver;
        private final BaseMinecraftOverlay  mOverlay;

        public MarkerLoader(Context aContext, BaseMinecraftOverlay aOverlay) {
            super(aContext);
            mContext = aContext;
            mOverlay = aOverlay;
            mObserver = new ForceLoadContentObserver();
        }

        @Override
        public void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<Marker> loadInBackground() {
            Cursor c = mOverlay.getCursor();

            mContext.getContentResolver().registerContentObserver(Marker.CONTENT_URI, true, mObserver);

            ArrayList<Marker> markers = new ArrayList<Marker>();

            while (c.moveToNext()) {
                Marker m = Marker.fromCursor(c);
                markers.add(m);
            }

            c.close();

            return markers;
        }

    }
}
