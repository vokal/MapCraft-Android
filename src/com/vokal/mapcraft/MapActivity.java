package com.vokal.mapcraft;

import android.content.*;
import android.os.*;
import android.view.Window;
import android.widget.SpinnerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;

import com.vokal.mapcraft.models.*;
import com.vokal.mapcraft.service.*;
import com.vokal.mapcraft.widget.TileSetNavAdapter;

public class MapActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {
    public static final int LOADER_NAV_LIST = 0;
    /** Called when the activity is first created. */

    private boolean mBound = false;
    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message aMsg) {
            switch (aMsg.what) {
                default:
                    super.handleMessage(aMsg);
            }
        }
    });
    private MapServiceConnection mService = new MapServiceConnection(mMessenger) {
        @Override
        public void onServiceConnected(ComponentName aName, IBinder aBinder) {
            super.onServiceConnected(aName, aBinder);

            Message msg = Message.obtain(null, MapSyncService.MSG_FETCH_CONFIG);
            msg.obj = mServer;
            send(msg);
        }
    };

    private NavLoaderManager mNavManager;

    Server mServer = new Server("TESET", "http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL");
    private SpinnerAdapter mAdapter;
    private MapFragment mMap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

        mNavManager = new NavLoaderManager();
        getSupportLoaderManager().initLoader(LOADER_NAV_LIST, null, mNavManager);

    }

    @Override
    public void onStart() {
        super.onStart();
        doBind();
    }

    @Override
    public void onStop() {
        super.onStop();
        doUnbind();
    }

    private void doBind() {
        bindService(new Intent(this, MapSyncService.class), mService, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    private void doUnbind() {
        if (mBound) {
            mService.unregister();
            unbindService(mService);
            mBound = false;
        }
    }

    private class NavLoaderManager implements LoaderManager.LoaderCallbacks<SpinnerAdapter> {
        public Loader<SpinnerAdapter> onCreateLoader(int aId, Bundle aArgs) {
            return new WorldTilesetNavLoader(MapActivity.this, mServer, mMap.getTileSet());
        }

        public void onLoadFinished(Loader<SpinnerAdapter> aLoader, SpinnerAdapter aData) {
            if (aData != null) {
                mAdapter = aData;
                getSupportActionBar().setListNavigationCallbacks(aData, MapActivity.this);
                getSupportActionBar().setSelectedNavigationItem(mMap.getSelectedIndex());
            }
        }

        public void onLoaderReset(Loader<SpinnerAdapter> aLoader) {
            mAdapter = null;
        }
    }

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
}
