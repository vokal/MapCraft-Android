package com.vokal.mapcraft;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.views.MapView;

import com.vokal.mapcraft.tileprovider.OverviewerTileSource;

public class MapActivity extends Activity 
{
    MapView mMap;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        mMap = (MapView) findViewById(R.id.mapview);
        mMap.setMultiTouchControls(true);

        final ITileSource tileSource = new OverviewerTileSource("Map Day", null, 0, 9, 384, "png",
            "http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL/world-lighting");
        mMap.setTileSource(tileSource);
    }
}
