package com.vokal.mapcraft.models;

import android.content.*;
import android.os.*;
import android.database.*;
import android.net.Uri;

import java.util.*;

import org.json.*;

import com.vokal.network.NetworkClient;
import com.vokal.network.NetworkResponse;

import com.vokal.mapcraft.MapCraftApplication;
import com.vokal.mapcraft.event.*;
import com.vokal.mapcraft.cp.MapcraftContentProvider;
import com.vokal.mapcraft.cp.MapcraftDBHelper;
import com.vokal.mapcraft.models.OverviewerMarker.MarkerGroup;

public class Server implements Parcelable {

    public static final String TAG = "SERVER";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String URL  = "url";
    public static final String PREVIEW =  "server_preview";

    public static final String[] ALL        = new String[] {
        ID, NAME, URL, PREVIEW
    };

    public static final Uri CONTENT_URI = Uri.parse("content://" +
        MapcraftContentProvider.AUTHORITY + "/" + MapcraftDBHelper.TABLE_SERVER);

    public static final String REGEX_FIND = "^var\\ *.*\\ *=\\ *\\{";

    private int mId = -1;
    private String mName;
    private String mUrl;
    private String mPreview;

    private HashSet<String> mTileSets;

    private Server() {

    }

    public Server(final String aName, final String aUrl) {
        mName = aName;
        mUrl = aUrl;
    }

    public String getName() {
        return mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(final String aUrl) {
        mUrl = aUrl;
    }

    public String getPreview() {
        return mPreview;
    }
    

    public void setup(final Context aContext) {
        MapCraftApplication.BUS.post(ProgressStateEvent.start());
        new Thread() {
            @Override
            public void run() {
                try {
                    fetchConfig(aContext);
                    MapCraftApplication.BUS.post(ProgressStateEvent.end());
                } catch (Exception e) {
                    MapCraftApplication.BUS.post(new ExceptionEvent());
                }
            }
        }.start();
    }

    private void fetchConfig(final Context aContext) throws Exception {
        NetworkClient client = NetworkClient.getInstance();
        NetworkResponse resp = client.get(mUrl + "/overviewerConfig.js");

        if (resp.getCode() / 100 == 2) {
            String config = resp.getResponse();
            JSONObject j = Server.jsToJSON(config);

            JSONObject consts = j.getJSONObject("CONST");
            int tileSize = consts.getInt("tileSize");

            JSONArray tilesets = j.getJSONArray("tilesets");
            ContentValues[] values = new ContentValues[tilesets.length()];
            mTileSets = new HashSet<String>();
            for (int i = 0; i < tilesets.length(); ++i) {

                TileSet set = new OverviewerTileSet(tilesets.getJSONObject(i));
                set.setTileSize(tileSize);
                set.setServerUrl(mUrl);

                if (mPreview == null) {
                    mPreview = set.getPreviewTile();
                    save(aContext);
                }

                values[i] = set.getContentValues();
                mTileSets.add(set.getRenderSet());
            }

            TileSet.saveBulk(aContext, values);
        }
    }

    public void save(Context aContext) {
        ContentValues values = getContentValues();

        Uri inserted = aContext.getContentResolver().insert(CONTENT_URI, values);
        try {
            mId = (int) ContentUris.parseId(inserted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    protected ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        if (mId != -1) {
            values.put(ID, mId);
        }

        values.put(NAME, mName);
        values.put(URL, mUrl);
        values.put(PREVIEW, mPreview);

        return values;
    }

    public static Server fromCursor(Cursor aCursor) {
        Server result = null;

        if (MapcraftContentProvider.isValidCursor(aCursor)) {
            result = new Server();

            int index = 0;
            for(String name : aCursor.getColumnNames()) {
                result.setByCursorColumn(aCursor, name, index);
                ++index;
            }
        }

        return result;
    }

    private void setByCursorColumn(final Cursor aCursor, final String aName, final int index) {
        if (aName.equals(ID)) {
            mId = aCursor.getInt(index);
        } else if (aName.equals(URL)) {
            mUrl = aCursor.getString(index);
        } else if (aName.equals(PREVIEW)) {
            mPreview = aCursor.getString(index);
        } else if (aName.equals(NAME)) {
            mName = aCursor.getString(index);
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
            for (String tileSet : mTileSets) {
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
                        groups.add(group);
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
                        JSONArray markerGroup = markersJSON.getJSONObject(group.groupName).getJSONArray("raw");
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

    public int describeContents() {
         return 0;
     }

     public void writeToParcel(Parcel out, int flags) {
         out.writeInt(mId);
         out.writeString(mUrl);
         out.writeString(mPreview);
         out.writeString(mName);
     }

     public static final Parcelable.Creator<Server> CREATOR
             = new Parcelable.Creator<Server>() {
         public Server createFromParcel(Parcel in) {
             return new Server(in);
         }

         public Server[] newArray(int size) {
             return new Server[size];
         }
     };
     
     private Server(Parcel in) {
         mId      = in.readInt();
         mUrl     = in.readString();
         mPreview = in.readString();
         mName    = in.readString();
     }
}
