package com.vokal.mapcraft.overlay;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;

import org.osmdroid.views.MapView;

import com.vokal.mapcraft.R;
import com.vokal.mapcraft.models.Marker;
import com.vokal.mapcraft.models.OverviewerTileSet;


public class OverviewerPlayers extends BaseOverviewerOverlay {

    public static final String  TAG             = "BaseOverviewerOverlay";

    public static final int     sDefaultMarker  = R.drawable.default_avatar;

    public OverviewerPlayers(FragmentActivity aContext, MapView aMapView, OverviewerTileSet aTileSet) {
        super(aContext, aMapView, aTileSet, sDefaultMarker);
    }

    @Override
    public String getTitle(Marker aMarker) {
        if (aMarker.mIcon != null && aMarker.mIcon.contains("/")) {
            return aMarker.mIcon.substring(aMarker.mIcon.lastIndexOf("/") + 1);
        } else {
            return "";
        }
    }

    @Override
    public String getDescription(Marker aMarker) {
        String location = String.format("X: %d\nY: %d\nZ: %d", aMarker.mX, aMarker.mY, aMarker.mZ);
        if (getTitle(aMarker).equals("")) {
            return aMarker.mText + "\n" + location;
        }
        return location;
    }

    @Override
    protected PlayerPin createItem(int pos) {
        Marker marker = mMarkerData.get(pos);
        return new PlayerPin(marker, getGeoPoint(marker), getTitle(marker));
    }

    @Override
    protected Cursor getCursor() {
        return mContext.getContentResolver().query(Marker.CONTENT_URI, Marker.ALL,
            Marker.WORLD + " = '" + mRenderSet + "' AND " + Marker.GROUP + " = 'Players'"
            , null, null);

    }



}
