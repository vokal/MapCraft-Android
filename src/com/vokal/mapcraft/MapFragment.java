package com.vokal.mapcraft;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

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

import com.vokal.mapcraft.models.OverviewerTileSet;
import com.vokal.mapcraft.models.TileSet;
import com.vokal.mapcraft.overlay.OverviewerPlayers;
import com.vokal.mapcraft.overlay.OverviewerSigns;
import com.vokal.mapcraft.tileprovider.TileSetTileSource;

public class MapFragment extends SherlockFragment {
    static final String TAG = MapFragment.class.getSimpleName();

    static IGeoPoint mLastCenter = null;
    static int mLastZoom = 6;

    RelativeLayout mParent;
    MapView mMap;
    MapController mController;

    TileSet mLastTileSet;
    int mSelectedIndex = 0;

    List<Overlay> mOverlays;

    MapListener mMapListener = new MapListener() {
        @Override
        public boolean onScroll(ScrollEvent aScroll) {
            mLastCenter = mMap.getMapCenter();
            return true;
        }

        @Override
        public boolean onZoom(ZoomEvent aZoom) {
            Log.d(TAG, "CURRENT_ZOOM: " +  aZoom.getZoomLevel());
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
            if (mLastTileSet != null) {
                final ITileSource tileSource = new TileSetTileSource(mLastTileSet);
                mMap.setTileSource(tileSource);
            }

            mController.setZoom(mLastZoom);
            mMap.setMapListener(mMapListener);

            if (mLastCenter != null) {
                mController.setCenter(mLastCenter);
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

        mController = mMap.getController();
        return content;
    }

    public void setTileSet(final TileSet aTileSet) {
        if (aTileSet != null && (mLastTileSet == null || !aTileSet.equals(mLastTileSet))) {
            final ITileSource tileSource = new TileSetTileSource(aTileSet);
            mMap.setTileSource(tileSource);
            
            if (mLastTileSet != null && !aTileSet.getWorldName().equals(mLastTileSet.getWorldName())) {
                mMap.getController().setZoom(0);
            }

            addOverlays(aTileSet);
        }
        mLastTileSet = aTileSet;
    }

    public TileSet getTileSet() {
        return mLastTileSet;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void setSelectedIndex(final int aSelectedIndex) {
        mSelectedIndex = aSelectedIndex;
    }

    @Override
    public void onCreateOptionsMenu(Menu aMenu, MenuInflater aInflater) {
        super.onCreateOptionsMenu(aMenu, aInflater);
        aInflater.inflate(R.menu.map_main, aMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aItem) {
        switch(aItem.getItemId()) {
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

    private void addOverlays(TileSet aTileSet) {
        if (mOverlays == null) {
            mOverlays = new ArrayList<Overlay>();
        }

        if (mOverlays.size() > 0) {
            mOverlays.clear();
            mMap.getOverlays().clear();
            // TODO recycle existing
        }

        // TODO: implement tile toggling/switching

        // TODO: add some method to switch on server type
        mOverlays.add(new OverviewerPlayers(this.getActivity(), mMap, (OverviewerTileSet) aTileSet));
        mOverlays.add(new OverviewerSigns(this.getActivity(), mMap, (OverviewerTileSet) aTileSet));

        mMap.getOverlays().addAll(mOverlays);
    }
}
