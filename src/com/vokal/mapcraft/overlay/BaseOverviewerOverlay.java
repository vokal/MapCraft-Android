package com.vokal.mapcraft.overlay;

import android.support.v4.app.FragmentActivity;

import microsoft.mappoint.TileSystem;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import com.vokal.mapcraft.models.*;

public abstract class BaseOverviewerOverlay extends BaseMinecraftOverlay {

    public static final String  TAG             = "BaseOverviewerOverlay";

    protected String mRenderSet;
    private TileSet.NorthDirection mNorthDirection;

    public BaseOverviewerOverlay(
            FragmentActivity aContext,
            MapView aMapView,
            OverviewerTileSet aTileSet,
            int aDefaultMarkerId) {

        super(aContext, aMapView, aTileSet, aDefaultMarkerId);

        mRenderSet      = aTileSet.getRenderSet();

        if (aTileSet.getNorthDirection() != null) {
            mNorthDirection = aTileSet.getNorthDirection();
        }
    }

    @Override
    protected GeoPoint getGeoPoint(Marker aMarker) {
        int x = aMarker.mX;
        int y = aMarker.mY;
        int z = aMarker.mZ;

        int temp;
        if (mNorthDirection != null) {
            switch(mNorthDirection) {
                case UPPER_RIGHT:
                    temp = x;
                    x = -z + 15;
                    z = temp;
                    break;
                case LOWER_RIGHT:
                    x = -x + 15;
                    z = -z + 15;
                    break;
                case LOWER_LEFT:
                    temp = x;
                    x = z;
                    z = -temp + 15;
                    break;
                case UPPER_LEFT:
                default:
            }
        }

        int pixelX = (12 * x) + (12 * z) + 12;                  // lng
        int pixelY = (-6 * x) + ( 6 * z) + (12 * (256 - y));    // lat

        pixelX *= 2;
        pixelY *= 2;

        pixelX += centerPixel - tileSize;
        pixelY += centerPixel;

        return TileSystem.PixelXYToLatLong(pixelX, pixelY, zoomLevels, null);

    }

}
