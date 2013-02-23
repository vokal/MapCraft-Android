package com.vokal.mapcraft.overlay;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import com.vokal.mapcraft.models.Marker;


public class PlayerPin extends OverlayItem {

    public static final String  TAG             = "PlayerPin";

    private String    mPlayerName;
    private Marker    mMarker;

    public PlayerPin(Marker marker, GeoPoint aGeoPoint, String aPlayerName) {
        super(aPlayerName, aPlayerName, marker.toString(), aGeoPoint);

        mPlayerName     = aPlayerName;
        mMarker         = marker;

        loadAvatar();
    }

    private void loadAvatar() {

        // TODO

    }

    private static String getUser(String aIconURL) {
        if (aIconURL != null && aIconURL.contains("/")) {
            return aIconURL.substring(aIconURL.lastIndexOf("/") + 1);
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        int x = (mGeoPoint.getLatitudeE6());
        int y = (mGeoPoint.getLongitudeE6());
        return String.format("(%d, %d): %s", x, y, mPlayerName);
    }

}
