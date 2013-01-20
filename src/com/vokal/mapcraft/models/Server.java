package com.vokal.mapcraft.models;

import android.content.ContentValues;
import android.content.Context;

import java.util.*;

import org.json.*;

import com.vokal.mapcraft.models.OverviewerMarker.MarkerGroup;
import com.vokal.network.NetworkClient;
import com.vokal.network.NetworkResponse;

public class Server {  // TODO: rename to OverviewerServer and derive from new Server base class

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String URL  = "url";

    public static final String REGEX_FIND = "^var\\ *.*\\ *=\\ *\\{";

    private String mName;
    private String mUrl;
    private HashSet<String> tileSets;

    public Server(final String aName, final String aUrl) {
        mName = aName;
        mUrl = aUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(final String aUrl) {
        mUrl = aUrl;
    }


    public void setup(final Context aContext) throws Exception {
        NetworkClient client = NetworkClient.getInstance();
        NetworkResponse resp = client.get(mUrl + "/overviewerConfig.js");

        if (resp.getCode() / 100 == 2) {
            String config = resp.getResponse();
            JSONObject j = Server.jsToJSON(config);

            JSONObject consts = j.getJSONObject("CONST");
            int tileSize = consts.getInt("tileSize");

            JSONArray tilesets = j.getJSONArray("tilesets");
            ContentValues[] values = new ContentValues[tilesets.length()];
            tileSets = new HashSet<String>();
            for (int i = 0; i < tilesets.length(); ++i) {
                TileSet set = new OverviewerTileSet(tilesets.getJSONObject(i));
                set.setTileSize(tileSize);
                set.setServerUrl(mUrl);
                values[i] = set.getContentValues();
                tileSets.add(set.getRenderSet());
            }

            TileSet.saveBulk(aContext, values);
        }
    }

    public void fetchMarkers(Context aContext) throws Exception {
        NetworkClient client = NetworkClient.getInstance();
        NetworkResponse resp = client.get(mUrl + "/markers.js");

        if (resp.getCode() / 100 == 2) {
            String config = resp.getResponse();
            JSONObject sets = Server.jsToJSON(config);

            HashMap<String, ArrayList<MarkerGroup>> markerGroups = new HashMap<String, ArrayList<MarkerGroup>>();
            int i = 0;
            for (String tileSet : tileSets) {
                if (sets.has(tileSet)) {
                    ArrayList<MarkerGroup> groups = new ArrayList<MarkerGroup>();
                    JSONArray region = sets.getJSONArray(tileSet);
                    for (int g = 0; g < region.length(); g++) {
                        JSONObject j = region.getJSONObject(g);
                        MarkerGroup group = new MarkerGroup();
                        group.world = tileSet;
                        group.displayName = j.getString("displayName");
                        group.groupName = j.getString("groupName");
                        group.icon = j.getString("icon");
                    }
                    markerGroups.put(tileSet, groups);
                }
                i++;
            }

            ArrayList<OverviewerMarker> markers = new ArrayList<OverviewerMarker>();

            resp = client.get(mUrl + "/markersDB.js");
            if (resp.getCode() / 100 == 2) {
                JSONObject markersJSON = Server.jsToJSON(resp.getResponse());

                for (String tileSet : markerGroups.keySet()) {
                    ArrayList<MarkerGroup> groups = markerGroups.get(tileSet);
                    for (MarkerGroup group : groups) {
                        JSONArray markerGroup = markersJSON.getJSONArray(group.groupName);
                        for (int m = 0; m < markerGroup.length(); m++) {
                            JSONObject markerJSON = markerGroup.getJSONObject(m);
                            markers.add(new OverviewerMarker(group, markerJSON));
                        }
                    }
                }

                ContentValues[] values = new ContentValues[markers.size()];
                int m = 0;
                for (OverviewerMarker marker : markers) {
                    values[m++] = marker.getContentValues();
                }
                OverviewerMarker.saveBulk(aContext, values);
            }
        }
    }

    public static JSONObject jsToJSON(final String aJSFile) throws JSONException {
        String out = aJSFile.replaceAll(REGEX_FIND, "{").replace("};", "}");
        return new JSONObject(out);
    }
}
