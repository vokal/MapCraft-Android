package com.vokal.mapcraft.service;

import android.app.Service;
import android.content.*;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.*;
import java.text.SimpleDateFormat;

import com.vokal.mapcraft.models.Server;

public class MapSyncService extends Service {
    public static final String TAG = "MapSyncService";

    public static final int MSG_REGISTER_CLIENT     = 0;
    public static final int MSG_UNREGISTER_CLIENT   = 1;
    public static final int MSG_FETCH_CONFIG        = 2;

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
            public void run() {
                try {
                    System.out.println("HEHRHRHEHRHERHHEHRHE");
                    aServer.setup(sContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
