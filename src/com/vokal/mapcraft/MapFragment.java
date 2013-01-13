package com.vokal.mapcraft;

import android.os.Bundle;
import android.view.*;

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
    MapView mMap;
    BoundingBoxE6 mLastCenter = null;

    MapListener mMapListener = new MapListener() {
        public boolean onScroll(ScrollEvent aScroll) {
            //TODO THIS DOESN:T WORK
            mLastCenter = mMap.getBoundingBox();
                
            return true;
        }

        public boolean onZoom(ZoomEvent aZoom) {
            mLastCenter = mMap.getBoundingBox();
            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (mLastCenter != null) {
            mMap.zoomToBoundingBox(mLastCenter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedState) {
        super.onCreateView(aInflater, aContainer, aSavedState);

        View content = aInflater.inflate(R.layout.map_fragment, null);

        mMap = (MapView) content.findViewById(R.id.mapview);
        mMap.setMultiTouchControls(true);
        mMap.setMapListener(mMapListener);

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
