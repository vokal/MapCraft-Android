package com.vokal.mapcraft.overlay;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

public class MarkersLoader {

    public static final String TAG = "MarkersLoader";

    private static MarkerFetcher sFetcher = null;
    private static Set<MarkersLoadedListener> sListeners;
    private static JSONObject sMarkers;
    private static long sTimeLoaded;

    public static void fetchMarkers(MarkersLoadedListener aCallback) {
        if (sListeners == null) {
            sListeners = new HashSet<MarkersLoadedListener>();
        }
        sListeners.add(aCallback);

        if (sFetcher == null) {
            sFetcher = new MarkerFetcher();
        }

        if (sFetcher.getStatus() == Status.PENDING) {
            sFetcher.execute();
        } else if (sFetcher.getStatus() == Status.FINISHED) {
            // TODO: check sTimeLoaded - now > DAY -> restart
            aCallback.onMarkersLoaded(sMarkers);
        } else {
            // Status.RUNNING
        }
    }

    private static class MarkerFetcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... aVoid) {

            try {
                URL url = new URL("http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL/markersDB.js");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream(), 4096);
                if (urlConnection.getResponseCode() / 100 == 2) {
                    String response = new Scanner(in).useDelimiter("\\A").next();
                    response.trim();
                    response = response.substring(0, response.length() - 1);
                    response = response.substring(response.indexOf("\n") - 1 );
                    sMarkers = new JSONObject(response);
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (sListeners != null) {
                for (MarkersLoadedListener listener : sListeners) {
                    if (listener != null) {
                        listener.onMarkersLoaded(sMarkers);
                    }
                }
            }
        }

    }

    public interface MarkersLoadedListener {
        public void onMarkersLoaded(JSONObject aMarkers);
    }



}
