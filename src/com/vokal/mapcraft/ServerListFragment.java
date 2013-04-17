package com.vokal.mapcraft;

import android.content.*;
import android.database.*;
import android.graphics.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.view.ViewGroup.LayoutParams;
import android.util.Log;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.vokal.mapcraft.models.Server;
import com.vokal.mapcraft.widget.*;

public class ServerListFragment extends SherlockListFragment {

    public static final int LOADER_SERVERS = 0;

    private ServerLoaderManager mNavManager;

    SimpleCursorAdapter mAdapter;
    SimpleCursorAdapter.ViewBinder mBinder = new SimpleCursorAdapter.ViewBinder() {
        public boolean setViewValue(View aView, Cursor aCursor, int aColumn) {
            switch(aView.getId()) {
                case R.id.title:
                    ((TextView) aView).setText(aCursor.getString(aColumn));
                    return true;
                case R.id.preview:
                    //dScoutApplication.sAvatarLoader.loadImage(aCursor.getString(aColumn),
                        //(ImageView) aView, R.drawable.profile_me_icon);
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public void onCreate(Bundle aSavedState) {
        super.onCreate(aSavedState);
    
        try {
            Server server = new Server("VOKAL Interactive", "http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL");
            server.save(getActivity());

            server = new Server("Wow", "http://thedailyautist.com/map");
            server.save(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getListView().setBackgroundColor(0xffffffff);

        mNavManager = new ServerLoaderManager();
        getLoaderManager().initLoader(LOADER_SERVERS, null, mNavManager);
    }

    @Override
    public void onListItemClick(ListView aList, View aView, int aPos, long aId) {
        if (mAdapter != null) {
            Cursor c = (Cursor) mAdapter.getItem(aPos);
            Server s = Server.fromCursor(c);

            Intent i = new Intent(getActivity(), MapActivity.class);
            i.putExtra(Server.TAG, s);
            startActivity(i);
        }
    }


    private class ServerLoaderManager implements LoaderManager.LoaderCallbacks<Cursor> {
        public Loader<Cursor> onCreateLoader(int aId, Bundle aArgs) {
            return new CursorLoader(getActivity(), Server.CONTENT_URI, Server.ALL,
                null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> aLoader, Cursor aData) {
            if (mAdapter == null) {
                mAdapter = new SimpleCursorAdapter(getActivity(), 
                    R.layout.server_item, 
                    aData,    
                    new String[] { Server.NAME, Server.PREVIEW },
                    new int[] { R.id.title, R.id.preview },
                    0);
                mAdapter.setViewBinder(mBinder);

                setListAdapter(mAdapter);
            } else {
                ((SimpleCursorAdapter) getListAdapter()).swapCursor(aData);
            }
        }

        public void onLoaderReset(Loader<Cursor> aLoader) {
            ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
        }
    }

}
