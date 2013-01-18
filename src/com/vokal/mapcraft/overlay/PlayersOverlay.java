package com.vokal.mapcraft.overlay;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.*;

import microsoft.mappoint.TileSystem;

import org.json.*;
import org.osmdroid.api.IMapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedOverlay;

import com.vokal.mapcraft.R;
import com.vokal.mapcraft.overlay.MarkersLoader.MarkersLoadedListener;

public class PlayersOverlay extends ItemizedOverlay<PlayerPin> {

    public static final String TAG = "PlayersOverlay";

    public static final int sDefaultMarker = R.drawable.default_avatar;

    public static DisplayMetrics metrics;
    private static double xDensity;
    private static double yDensity;

    private List<PlayerPosition> mPlayers = new ArrayList<PlayerPosition>();

    // TODO: get this from config
    int zoomLevels = 11;

    int mapSize = TileSystem.MapSize(zoomLevels);
    int centerPixel = mapSize / 2;
    int tileSize = TileSystem.getTileSize();
    int numTiles = mapSize / tileSize;

    public PlayersOverlay(Context aContext, MapView aMapView) {
        super(aContext.getResources().getDrawable(sDefaultMarker), aMapView.getResourceProxy());
        Log.w(TAG, "-------------------------------------------------------------");
        MarkersLoader.fetchMarkers(new PlayersLoadedListener());

        metrics = new DisplayMetrics();
        WindowManager mgr = (WindowManager) aContext.getSystemService(Context.WINDOW_SERVICE);
        mgr.getDefaultDisplay().getMetrics(metrics);
        xDensity = metrics.xdpi / 160;
        yDensity = metrics.ydpi / 160;
        Log.d(TAG, String.format("Densities: (%f, %f)", xDensity, yDensity));

    }

    @Override
    public boolean onSnapToItem(int arg0, int arg1, Point aPoint, IMapView aMapView) {
        Log.d(TAG, String.format("onSnapToItem(%d, %d, %s)", arg0, arg1, aPoint.toString()));
        return false;
    }

    @Override
    protected PlayerPin createItem(int pos) {
        Log.i(TAG, "-------------------------------------------------------------");
        Log.i(TAG, mPlayers.get(pos).toString());
        return new PlayerPin(mPlayers.get(pos));
    }

    @Override
    public int size() {
        return mPlayers.size();
    }

    private class PlayersLoadedListener implements MarkersLoadedListener {

        @Override
        public void onMarkersLoaded(JSONObject aMarkers) {
            Iterator<String> names = aMarkers.keys();
            while (names.hasNext()) {
                String name = names.next();
                try {
                    JSONObject obj = aMarkers.getJSONObject(name);
                    if (obj.get("name").equals("Players")) {
//                        Log.d(TAG, "Players: " + obj.getJSONArray("raw"));
                        loadPlayers(obj.getJSONArray("raw"));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

//                Log.d(TAG, "mapSize: " + mapSize);
//                Log.d(TAG, "numtiles: " + mapSize / tileSize);
//
//                double pixels = tileSize * Math.pow(2, zoomLevels);
//                Log.d(TAG, "pixels: " + pixels);
//
//                Log.d(TAG, "perPixel: " + 1.0 / mapSize);
            }
        }

    }

    private void loadPlayers(JSONArray aPlayers) throws JSONException {
        int count = aPlayers.length();
        for (int i=0; i<count; i++) {
            JSONObject data = aPlayers.getJSONObject(i);
            PlayerPosition player = new PlayerPosition();
            player.name = data.getString("text");
            player.icon = data.getString("icon");
            player.x = data.getInt("x");
            player.y = data.getInt("y");
            player.z = data.getInt("z");
            mPlayers.add(player);
        }

        PlayerPosition test = new PlayerPosition();
        test.name = "test";
        test.icon = "/test";
        test.x = -1805;
        test.y = 90;
        test.z = 2225;
        mPlayers.add(test);

        this.populate();
    }

    public class PlayerPosition {
        String name;
        String icon;

        String realm;
        int x, y, z;

        public GeoPoint getGeoPoint() {
            return fromMinecraftPosition(x, y, z);
        }

        @Override
        public String toString() {
            return String.format("(%d, %d, %d) %s", x, y, z, name);
        }
    }

    private GeoPoint fromMinecraftPosition(int x, int y, int z) {

        /*
         * 'fromWorldToLatLng': function(x, y, z, model) {

            var zoomLevels = model.get("zoomLevels");
            var north_direction = model.get('north_direction');             */



        double perPixel = 1.0 / (tileSize * Math.pow(2, zoomLevels));
        /*
        if (north_direction == overviewerConfig.CONST.UPPERRIGHT){
            temp = x;
            x = -z+15;
            z = temp;
        } else if(north_direction == overviewerConfig.CONST.LOWERRIGHT){
            x = -x+15;
            z = -z+15;
        } else if(north_direction == overviewerConfig.CONST.LOWERLEFT){
            temp = x;
            x = z;
            z = -temp+15;
        } */

        // TODO: once we have north_direction from config



        // This information about where the center column is may change with
        // a different drawing implementation -- check it again after any
        // drawing overhauls!

        // point (0, 0, 127) is at (0.5, 0.0) of tile (tiles/2 - 1, tiles/2)
        // so the Y coordinate is at 0.5, and the X is at 0.5 -
        // ((tileSize / 2) / (tileSize * 2^zoomLevels))
        // or equivalently, 0.5 - (1 / 2^(zoomLevels + 1))
//        var lng = 0.5 - (1.0 / Math.pow(2, zoomLevels + 1));
//        var lat = 0.5;

        double lat = 0.5;
        double lng = 0.5 - (1.0 / Math.pow(2,  zoomLevels + 1));

        Log.i(TAG, String.format("initial lat, lng (%f, %f)", lat, lng));

        // the following metrics mimic those in
        // chunk_render in src/iterate.c

        // each block on X axis adds 12px to x and subtracts 6px from y
        lng += 12 * x * perPixel;// * xDensity;
        lat -= 6 * x * perPixel;// * yDensity;

        // each block on Y axis adds 12px to x and adds 6px to y
        lng += 12 * z * perPixel;// * xDensity;
        lat += 6 * z * perPixel;// * yDensity;

        // each block down along Z adds 12px to y
        lat += 12 * (256 - y) * perPixel;// * yDensity;

        // add on 12 px to the X coordinate to center our point
        lng += 12 * perPixel;// * xDensity;

//        int pixelX = mapSize / 2;
//        int pixelY = mapSize / 2;

        int pixelX = (12 * x) + (12 * z) + 12;                  // lng
        int pixelY = (-6 * x) + ( 6 * z) + (12 * (256 - y));    // lat

        pixelX *= 2;
        pixelY *= 2;

//        pixelX += centerPixel - Math.sqrt(numTiles * tileSize) / metrics.density;
//        pixelX += centerPixel - Math.sqrt(numTiles * 2) * metrics.density;
        pixelX += centerPixel - tileSize;
        pixelY += centerPixel;

        Log.d(TAG, String.format("(%d, %d) pixelXY", pixelX, pixelY));

        GeoPoint geo = new GeoPoint(0,0);
        TileSystem.PixelXYToLatLong(pixelX, pixelY, zoomLevels, geo);

        Point reuse = new Point();
        TileSystem.PixelXYToTileXY(pixelX, pixelY, reuse);

//        Log.d(TAG, "tile: " + reuse);
//        Log.d(TAG, " geo: " + geo);

        lat = (180 * lat) - 90;
        lng = (360 * lng) - 180;
//        Log.d(TAG, String.format("(%f, %f)", lat, lng));

        return geo;
//        return new GeoPoint(-lat, lng);
    }

}
