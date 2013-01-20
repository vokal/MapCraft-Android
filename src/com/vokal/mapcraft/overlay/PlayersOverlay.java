package com.vokal.mapcraft.overlay;

import android.content.Context;

import java.util.ArrayList;
import java.util.Iterator;

import microsoft.mappoint.TileSystem;

import org.json.*;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import com.vokal.mapcraft.R;
import com.vokal.mapcraft.models.TileSet;
import com.vokal.mapcraft.overlay.MarkersLoader.MarkersLoadedListener;

public class PlayersOverlay extends BaseMinecraftOverlay {

    public static final String TAG = "PlayersOverlay";

    public static final int sDefaultMarker = R.drawable.default_avatar;

    private ArrayList<PlayerPosition> mPlayers = new ArrayList<PlayerPosition>();

    public PlayersOverlay(Context aContext, MapView aMapView, TileSet aTileSet) {
        super(aContext.getResources().getDrawable(sDefaultMarker), aMapView, aTileSet);
        MarkersLoader.fetchMarkers(new PlayersLoadedListener());
    }

    @Override
    protected PlayerPin createItem(int pos) {
        return new PlayerPin(mPlayers.get(pos));
    }

    @Override
    public int size() {
        return mPlayers.size();
    }

    private class PlayersLoadedListener implements MarkersLoadedListener {

        @Override
        public void onMarkersLoaded(JSONObject aMarkers) {
            Iterator<String> keys = aMarkers.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    JSONObject obj = aMarkers.getJSONObject(key);
                    if (obj.get("name").equals("Players")) {

                        loadPlayers(obj.getJSONArray("raw"));
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
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

        // TODO: once we have north_direction from config
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

        int pixelX = (12 * x) + (12 * z) + 12;                  // lng
        int pixelY = (-6 * x) + ( 6 * z) + (12 * (256 - y));    // lat

        pixelX *= 2;
        pixelY *= 2;

        pixelX += centerPixel - tileSize;
        pixelY += centerPixel;

        return TileSystem.PixelXYToLatLong(pixelX, pixelY, zoomLevels, null);
    }

}
