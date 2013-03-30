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

import com.squareup.otto.Subscribe;

import com.vokal.mapcraft.models.Server;
import com.vokal.mapcraft.event.ProgressStateEvent;

public class ServerActivity extends SherlockFragmentActivity {    

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MapCraftApplication.BUS.register(this);

        FragmentManager manager = getSupportFragmentManager();
        Fragment frag = manager.findFragmentById(R.id.map_fragment);
        if (frag == null) {
            FragmentTransaction trans = manager.beginTransaction();
            ServerListFragment list = new ServerListFragment();
            trans.replace(R.id.map_fragment, list);
            trans.commit();
        }
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
