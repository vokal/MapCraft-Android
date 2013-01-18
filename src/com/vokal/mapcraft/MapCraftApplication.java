package com.vokal.mapcraft;

import android.app.Application;

import com.vokal.network.NetworkClient;
import com.vokal.network.HttpURLConnectionImpl;

public class MapCraftApplication extends Application {

    @Override 
    public void onCreate() {
        super.onCreate();

        NetworkClient client = NetworkClient.getInstance();
        client.setImpl(new HttpURLConnectionImpl(""));
    }
}
