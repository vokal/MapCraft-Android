package com.vokal.mapcraft.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import java.util.ArrayList;

import com.vokal.mapcraft.models.Server;

public class MapSyncService extends Service {
    public static final String TAG = "MapSyncService";

    public static final int MSG_REGISTER_CLIENT     = 0;
    public static final int MSG_UNREGISTER_CLIENT   = 1;
    public static final int MSG_FETCH_CONFIG        = 2;
    public static final int MSG_FETCH_MARKERS       = 3;

    private static final ArrayList<Messenger> sClients = new ArrayList<Messenger>();
    private static final MessageHandler sHandler       = new MessageHandler();
    private static Messenger sMessenger   = new Messenger(sHandler);

    private static Context sContext;

    @Override
    public IBinder onBind(Intent intent) {
        sContext = this;
        return sMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
        sContext = this;
        return Service.START_STICKY;
    }

    public static class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message aMsg) {
            switch (aMsg.what) {
                case MSG_REGISTER_CLIENT:
                    registerClient(aMsg);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    unregisterClient(aMsg);
                    break;
                case MSG_FETCH_CONFIG:
                    fetchConfig((Server) aMsg.obj);
                    break;
                case MSG_FETCH_MARKERS:
                    fetchMarkers((Server) aMsg.obj);
                    break;
            }
        }
    }

    private static synchronized void registerClient(Message aMsg) {
        sClients.add(aMsg.replyTo);
    }

    private static synchronized void unregisterClient(Message aMsg) {
        sClients.remove(aMsg.replyTo);
    }

    private static synchronized void sendMessage(int aMsg, int aArg) {
        for (Messenger client : sClients) {
            try {
                Message msg = Message.obtain(null, aMsg, aArg, 0);
                client.send(msg);
            } catch (RemoteException e) {
                sClients.remove(client);
            }
        }
    }

    private static void fetchConfig(final Server aServer) {
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("HEHRHRHEHRHERHHEHRHE");
                    aServer.setup(sContext);
                    Message msg = Message.obtain(sHandler, MSG_FETCH_MARKERS);
                    msg.obj = aServer;
                    sMessenger.send(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private static void fetchMarkers(final Server aServer) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "FETCHING MARKERS");
                    aServer.fetchMarkers(sContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
