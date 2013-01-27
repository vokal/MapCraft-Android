package com.vokal.mapcraft.models;

import org.json.JSONObject;

public class OverviewerMarker extends Marker {

    public static final String TAG = OverviewerMarker.class.getSimpleName();

    public static class MarkerGroup {
        String world;
        String displayName;
        String groupName;
        String icon;
    }

    OverviewerMarker() { }

    OverviewerMarker(MarkerGroup aGroup, JSONObject markerJSON) throws Exception {
        mWorld = aGroup.world;
        mGroup = aGroup.displayName;

        mText = markerJSON.getString(TEXT);
        mX = markerJSON.getInt(X);
        mY = markerJSON.getInt(Y);
        mZ = markerJSON.getInt(Z);

        if (markerJSON.has(ICON)) {
            mIcon = markerJSON.getString(ICON);
        } else {
            mIcon = aGroup.icon;
        }
    }



}
