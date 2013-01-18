package com.vokal.mapcraft.overlay;

import android.content.Context;
import android.graphics.Point;

import java.util.ArrayList;

import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;

import com.vokal.mapcraft.R;

public class SignsOverlay extends ItemizedOverlay<SignPin> {

    ArrayList<SignPin> signs;

    public SignsOverlay(Context aContext, MapView aMapView) {
        super(aContext.getResources().getDrawable(R.drawable.signpost), aMapView.getResourceProxy());

        signs = new ArrayList<SignPin>();

        signs.add(new SignPin("", "", new GeoPoint(0,0)));
        signs.add(new SignPin("", "", new GeoPoint(-80,-180)));
        signs.add(new SignPin("", "", new GeoPoint(80,180)));
        populate();
    }

    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point arg2, IMapView arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected SignPin createItem(int arg0) {
        // TODO Auto-generated method stub
        return signs.get(arg0);
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return signs.size();
    }

}
