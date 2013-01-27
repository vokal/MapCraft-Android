package com.vokal.mapcraft.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.*;

public class MapServiceConnection implements ServiceConnection {
    private Messenger mService;
    private Messenger mClient;

    public MapServiceConnection(Messenger aClient) {
        mClient = aClient;
    }

    public void onServiceConnected(ComponentName aName, IBinder aBinder) {
        mService = new Messenger(aBinder);
        register();
    }

    public void onServiceDisconnected(ComponentName aName) {
        mService = null;
    }

    public void send(Message aMsg) {
        if (mService != null) {
            try {
                mService.send(aMsg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void register() {
        Message msg = Message.obtain(null, MapSyncService.MSG_REGISTER_CLIENT);
        msg.replyTo = mClient;
        send(msg);
    }

    public void unregister() {
        Message msg = Message.obtain(null, MapSyncService.MSG_UNREGISTER_CLIENT);
        msg.replyTo = mClient;
        send(msg);
    }
};
