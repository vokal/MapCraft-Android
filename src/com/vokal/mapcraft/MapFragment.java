package com.vokal.mapcraft;

import android.os.Bundle;
import android.view.*;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.*;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.vokal.mapcraft.overlay.SignsOverlay;
import com.vokal.mapcraft.overlay.PlayersOverlay;
import com.vokal.mapcraft.tileprovider.OverviewerTileSource;

public class MapFragment extends SherlockFragment {
    static final String TAG = MapFragment.class.getSimpleName();

    static IGeoPoint mLastCenter = null;
    static int mLastZoom = 6;

    MapView mMap;
    MapController mController;
    List<Overlay> mOverlays;

    MapListener mMapListener = new MapListener() {
        @Override
        public boolean onScroll(ScrollEvent aScroll) {
            mLastCenter = mMap.getMapCenter();
            return true;
        }

        @Override
        public boolean onZoom(ZoomEvent aZoom) {
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

        mController.setZoom(mLastZoom);

        if (mLastCenter != null) {
            mController.setCenter(mLastCenter);
        }

        mMap.setMapListener(mMapListener);
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

        final ITileSource tileSource = new OverviewerTileSource("Map Day", null, 0, 10, 384, "png",
            "http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL/world-lighting");
        mMap.setTileSource(tileSource);

        mController = mMap.getController();
        addOverlays();

        return content;
    }

    @Override
    public void onCreateOptionsMenu(Menu aMenu, MenuInflater aInflater) {
        super.onCreateOptionsMenu(aMenu, aInflater);
        aInflater.inflate(R.menu.map_main, aMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aItem) {
        switch (aItem.getItemId()) {
            case R.id.reload_cache:
                reloadCache();
                return true;
            default:
                return super.onOptionsItemSelected(aItem);
        }
    }

    private void reloadCache() {
        if (mMap != null) {
            mMap.getTileProvider().clearTileCache();
            mMap.invalidate();
        }
    }

    @Override
    public void onActivityCreated(Bundle aSavedState) {
        super.onActivityCreated(aSavedState);

        setRetainInstance(true);
    }

    private void addOverlays() {
        if (mOverlays == null) {
            mOverlays = new ArrayList<Overlay>();
        }
        
        if (mOverlays.size() > 0) {
            mOverlays.clear();
            // TODO recycle existing
        }
    
        mOverlays.add(new SignsOverlay(this.getActivity(), mMap));
        mOverlays.add(new PlayersOverlay(this.getActivity(), mMap));
    
        mMap.getOverlays().addAll(mOverlays);
    }
}
