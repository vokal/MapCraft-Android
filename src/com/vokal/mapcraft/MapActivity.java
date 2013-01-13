package com.vokal.mapcraft;

import android.os.Bundle;
import android.view.Window;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;

public class MapActivity extends SherlockFragmentActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(R.id.map_fragment) == null) {
            FragmentTransaction trans = manager.beginTransaction();
            trans.replace(R.id.map_fragment, new MapFragment());
            trans.commit();
        }
    }
}
