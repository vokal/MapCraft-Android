package com.vokal.mapcraft.overlay;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import com.vokal.mapcraft.models.Marker;

public class SignPin extends OverlayItem {

    public static final String  TAG     = "SignPin";

    private Marker mMarker;

    public SignPin(Marker aMarker, GeoPoint aGeoPoint, String aTitle) {
        super(String.valueOf(aMarker.hashCode()), aTitle, aMarker.mText, aGeoPoint);
        mMarker = aMarker;
    }

}
