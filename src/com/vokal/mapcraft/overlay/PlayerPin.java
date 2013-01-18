package com.vokal.mapcraft.overlay;

import android.util.Log;

import org.osmdroid.views.overlay.OverlayItem;

import com.vokal.mapcraft.overlay.PlayersOverlay.PlayerPosition;


public class PlayerPin extends OverlayItem {

    public static final String TAG = "PlayerPin";

    private String userName;
    private PlayerPosition playerPos;

    public PlayerPin(PlayerPosition aPlayer) {
        super(getUser(aPlayer.icon), aPlayer.name, "", aPlayer.getGeoPoint());
        userName = getUser(aPlayer.icon);
        playerPos = aPlayer;
        setMarkerHotspot(HotspotPlace.BOTTOM_CENTER);
        Log.d(TAG, this.toString());
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
        return String.format("(%d, %d): %s", x, y, userName);
    }

}
