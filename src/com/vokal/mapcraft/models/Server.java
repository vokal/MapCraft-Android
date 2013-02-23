package com.vokal.mapcraft.models;

import android.content.*;
import android.os.*;
import android.database.*;
import android.net.Uri;

import org.json.*;

import com.vokal.network.NetworkClient;
import com.vokal.network.NetworkResponse;

import com.vokal.mapcraft.cp.MapcraftContentProvider;
import com.vokal.mapcraft.cp.MapcraftDBHelper;

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

                if (mPreview == null) {
                    mPreview = set.getPreviewTile();
                    save(aContext);
                }

                values[i] = set.getContentValues();
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
