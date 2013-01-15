package com.vokal.mapcraft;

import android.content.*;
import android.os.*;
import android.view.Window;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;

import com.vokal.mapcraft.models.*;
import com.vokal.mapcraft.service.*;

public class MapActivity extends SherlockFragmentActivity {
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

    Server mServer = new Server("TESET", "http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL");

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

    }

    @Override
    public void onStart() {
        super.onStart();
        doBind();
    }

    @Override
    public void onResume() {
        super.onResume();
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
