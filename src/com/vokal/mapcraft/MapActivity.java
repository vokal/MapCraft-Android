package com.vokal.mapcraft;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.Loader;
import android.widget.SpinnerAdapter;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import com.squareup.otto.Subscribe;

import com.vokal.mapcraft.models.*;
import com.vokal.mapcraft.event.ProgressStateEvent;
import com.vokal.mapcraft.widget.TileSetNavAdapter;

public class MapActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {
    public static final int LOADER_NAV_LIST = 0;
    /** Called when the activity is first created. */

    private NavLoaderManager mNavManager;

    Server mServer;
    private SpinnerAdapter mAdapter;
    private MapFragment mMap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        MapCraftApplication.BUS.register(this);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentById(R.id.map_fragment);
        if (frag == null) {
            FragmentTransaction trans = manager.beginTransaction();
            mMap = new MapFragment();
            trans.replace(R.id.map_fragment, mMap);
            trans.commit();
        } else {
            mMap = (MapFragment) frag;
        }

        mServer = (Server) getIntent().getParcelableExtra(Server.TAG);
        mServer.setup(getApplicationContext());

        mNavManager = new NavLoaderManager();
        getSupportLoaderManager().initLoader(LOADER_NAV_LIST, null, mNavManager);

    }

    private class NavLoaderManager implements LoaderManager.LoaderCallbacks<SpinnerAdapter> {
        @Override
        public Loader<SpinnerAdapter> onCreateLoader(int aId, Bundle aArgs) {
            return new WorldTilesetNavLoader(MapActivity.this, mServer, mMap.getTileSet());
        }

        @Override
        public void onLoadFinished(Loader<SpinnerAdapter> aLoader, SpinnerAdapter aData) {
            if (aData != null) {
                mAdapter = aData;
                getSupportActionBar().setListNavigationCallbacks(aData, MapActivity.this);
                getSupportActionBar().setSelectedNavigationItem(mMap.getSelectedIndex());
            }
        }

        @Override
        public void onLoaderReset(Loader<SpinnerAdapter> aLoader) {
            mAdapter = null;
        }
    }

    @Override
    public boolean onNavigationItemSelected(int aPos, long aId) {
        if (mAdapter != null) {
            TileSet t = ((TileSetNavAdapter) mAdapter).getItem(aPos);
            if (!t.equals(mMap.getTileSet())) {
                mMap.setTileSet(t);
                mMap.setSelectedIndex(aPos);
                getSupportLoaderManager().restartLoader(LOADER_NAV_LIST, null, mNavManager);
            }
            return true;
        }

        return false;
    }

    @Subscribe 
    public void updateProgress(final ProgressStateEvent aEvent) {
        runOnUiThread( new Runnable() {
            public void run() {
                setSupportProgressBarIndeterminateVisibility(aEvent.isRunning());
            }
        });
    }
}
