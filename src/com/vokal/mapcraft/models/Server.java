package com.vokal.mapcraft.models;

import android.content.Context;
import android.content.ContentValues;

import org.json.*;

import com.vokal.network.*;

public class Server {

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String URL  = "url";

    public static final String REGEX_FIND = "^var\\ *.*\\ *=\\ *\\{";

    public String mName;
    public String mUrl;

    public Server(final String aName, final String aUrl) {
        mName = aName;
        mUrl = aUrl;
    }

    public void setup(final Context aContext) throws Exception {
        NetworkClient client = NetworkClient.getInstance(); 
        client.setImpl(new HttpURLConnectionImpl(""));
        NetworkResponse resp = client.get(mUrl + "/overviewerConfig.js");

        if (resp.getCode() / 100 == 2) {
            String config = resp.getResponse();
            config = config.replaceAll(REGEX_FIND, "{").replace("};", "}");
            JSONObject j = new JSONObject(config);

            System.out.println(j);

            JSONArray tilesets = j.getJSONArray("tilesets");
            ContentValues[] values = new ContentValues[tilesets.length()];
            for (int i = 0; i < tilesets.length(); ++i) {
                TileSet set = new OverviewerTileSet(tilesets.getJSONObject(i));
                set.setServerUrl(mUrl);
                values[i] = set.getContentValues();

                System.out.println(set.toString());
            }

            TileSet.saveBulk(aContext, values);
        }
    }
}
