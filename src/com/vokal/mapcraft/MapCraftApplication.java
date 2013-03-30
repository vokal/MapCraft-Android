package com.vokal.mapcraft;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import com.vokal.network.NetworkClient;
import com.vokal.network.HttpURLConnectionImpl;

public class MapCraftApplication extends Application {

    public static final Bus BUS = new Bus(ThreadEnforcer.ANY);

    @Override 
    public void onCreate() {
        super.onCreate();

        NetworkClient client = NetworkClient.getInstance();
        client.setImpl(new HttpURLConnectionImpl(""));
    }
}
