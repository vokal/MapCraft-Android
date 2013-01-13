package com.vokal.mapcraft;

import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.util.Log;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.ActionBar;

import microsoft.mappoint.TileSystem;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.*;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;

import com.vokal.mapcraft.tileprovider.OverviewerTileSource;

public class MapFragment extends Fragment {
    static final String TAG = MapFragment.class.getSimpleName();
    MapView mMap;
    int mLastZoom         = 0;
    IGeoPoint mLastCenter = null;

    MapListener mMapListener = new MapListener() {
        public boolean onScroll(ScrollEvent aScroll) {
            mLastCenter = mMap.getProjection().fromPixels(mMap.getWidth() / 2, mMap.getHeight() / 2);        
            return true;
        }

        public boolean onZoom(ZoomEvent aZoom) {
            mLastZoom = aZoom.getZoomLevel();
            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        mMap.getController().setZoom(mLastZoom);

        if (mLastCenter != null) {
            mMap.getController().setCenter(mLastCenter);
        }
        mMap.setMapListener(mMapListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        mMap.setMapListener(null);
    }

    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedState) {
        super.onCreateView(aInflater, aContainer, aSavedState);

        View content = aInflater.inflate(R.layout.map_fragment, null);

        mMap = (MapView) content.findViewById(R.id.mapview);
        mMap.setMultiTouchControls(true);

        final ITileSource tileSource = new OverviewerTileSource("Map Day", null, 0, 10, 384, "png",
            "http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL/world-lighting");
        mMap.setTileSource(tileSource);

        return content;
    }

    @Override
    public void onActivityCreated(Bundle aSavedState) {
        super.onActivityCreated(aSavedState);

        setRetainInstance(true);
    }
}