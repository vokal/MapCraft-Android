package com.vokal.mapcraft.overlay;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;

import com.vokal.mapcraft.R;
import com.vokal.mapcraft.models.Marker;
import com.vokal.mapcraft.models.OverviewerTileSet;

public class OverviewerSigns extends BaseOverviewerOverlay {

    public static final String  TAG     = "OverviewerSigns";

    public static final int     sDefaultMarker  = R.drawable.signpost;

    public OverviewerSigns(FragmentActivity aContext, MapView aMapView, OverviewerTileSet aTileSet) {
        super(aContext, aMapView, aTileSet, sDefaultMarker);
    }


    @Override
    protected String getTitle(Marker aMarker) {
        return "";
    }

    @Override
    protected String getDescription(Marker aMarker) {
        return aMarker.mText;
    }

    @Override
    protected Cursor getCursor() {
        return mContext.getContentResolver().query(Marker.CONTENT_URI, Marker.ALL,
            Marker.WORLD + " = '" + mRenderSet + "' AND " + Marker.GROUP + " = 'All signs'"
            , null, null);
    }


    @Override
    protected OverlayItem createItem(int pos) {
        Marker marker = mMarkerData.get(pos);
        return new SignPin(marker, getGeoPoint(marker), getTitle(marker));
    }

}
