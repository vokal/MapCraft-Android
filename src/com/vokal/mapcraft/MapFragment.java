package com.vokal.mapcraft;

import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import microsoft.mappoint.TileSystem;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.*;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;

import com.vokal.mapcraft.models.TileSet;
import com.vokal.mapcraft.tileprovider.*;

public class MapFragment extends SherlockFragment {
    static final String TAG = MapFragment.class.getSimpleName();
    
    static IGeoPoint mLastCenter = null;
    static int mLastZoom         = 0;

    RelativeLayout mParent;
    MapView mMap;
    TileSet mLastTileSet;
    
    MapListener mMapListener = new MapListener() {
        public boolean onScroll(ScrollEvent aScroll) {
            mLastCenter = mMap.getMapCenter();
            return true;
        }

        public boolean onZoom(ZoomEvent aZoom) {
            android.util.Log.d(TAG, "CURRENT_ZOOM: " +  aZoom.getZoomLevel());
            mLastZoom = aZoom.getZoomLevel();
            return true;
        }
    };

    @Override
    public void onCreate(Bundle aSavedState) {
        super.onCreate(aSavedState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap != null) {
            mMap.getController().setZoom(mLastZoom);

            if (mLastCenter != null) {
                mMap.getController().setCenter(mLastCenter);
            }
            mMap.setMapListener(mMapListener);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mMap.setMapListener(null);
        
        mLastCenter = mMap.getMapCenter();
    }

    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedState) {
        super.onCreateView(aInflater, aContainer, aSavedState);

        View content = aInflater.inflate(R.layout.map_fragment, null);

        mMap = (MapView) content.findViewById(R.id.mapview);
        mMap.setMultiTouchControls(true);
        mMap.setMapListener(mMapListener);

        return content;
    }

    public void setTileSet(final TileSet aTileSet) {
        if (mLastTileSet == null || !aTileSet.equals(mLastTileSet)) {
            final ITileSource tileSource = new TileSetTileSource(aTileSet, 384);
            mMap.setTileSource(tileSource);
        }
        mLastTileSet = aTileSet;
    }

    @Override
    public void onCreateOptionsMenu(Menu aMenu, MenuInflater aInflater) {
        super.onCreateOptionsMenu(aMenu, aInflater);
        aInflater.inflate(R.menu.map_main, aMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aItem) {
        switch(aItem.getItemId()) {
            case R.id.reload_cache:
                reloadCache();
                return true;
            default:
                return super.onOptionsItemSelected(aItem);
        }
    }

    private void reloadCache() {
        if  (mMap != null) {
            mMap.getTileProvider().clearTileCache();
        }
    }

    @Override
    public void onActivityCreated(Bundle aSavedState) {
        super.onActivityCreated(aSavedState);

        setRetainInstance(true);
    }
}
