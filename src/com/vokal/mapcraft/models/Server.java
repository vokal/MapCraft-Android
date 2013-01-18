package com.vokal.mapcraft.models;

import android.content.Context;
import android.content.ContentValues;

import org.json.*;

import com.vokal.network.NetworkClient;
import com.vokal.network.NetworkResponse;

public class Server {

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String URL  = "url";

    public static final String REGEX_FIND = "^var\\ *.*\\ *=\\ *\\{";

    private String mName;
    private String mUrl;

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

            JSONArray tilesets = j.getJSONArray("tilesets");
            ContentValues[] values = new ContentValues[tilesets.length()];
            for (int i = 0; i < tilesets.length(); ++i) {
                TileSet set = new OverviewerTileSet(tilesets.getJSONObject(i));
                set.setServerUrl(mUrl);
                values[i] = set.getContentValues();
            }

            TileSet.saveBulk(aContext, values);
        }
    }

    public static JSONObject jsToJSON(final String aJSFile) throws JSONException {
        String out = aJSFile.replaceAll(REGEX_FIND, "{").replace("};", "}");
        return new JSONObject(out);
    }
}
