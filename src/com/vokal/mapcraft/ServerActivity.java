package com.vokal.mapcraft;

import android.content.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;

import android.support.v4.app.*;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.vokal.mapcraft.models.Server;
import com.vokal.mapcraft.service.*;

public class ServerActivity extends SherlockFragmentActivity {    

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
    private MapServiceConnection mService = new MapServiceConnection(mMessenger);

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentById(R.id.map_fragment);
        if (frag == null) {
            FragmentTransaction trans = manager.beginTransaction();
            ServerListFragment list = new ServerListFragment();
            trans.replace(R.id.map_fragment, list);
            trans.commit();
        }
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
}
