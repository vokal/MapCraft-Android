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
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.vokal.mapcraft.models.Server;
import com.vokal.mapcraft.widget.*;

public class ServerListFragment extends SherlockFragment 
    implements SwipeDismissListViewTouchListener.OnDismissCallback {

    public static final int LOADER_SERVERS = 0;

    private ServerLoaderManager mNavManager;
    
    ListView mList;
    SwipeDismissListViewTouchListener mTouchListener;
    ServerAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater aInflater, ViewGroup aContainer, Bundle aSavedState) {
        super.onCreateView(aInflater, aContainer, aSavedState);

        try {
            Server server = new Server("VOKAL Interactive", "http://s3-us-west-2.amazonaws.com/vokal-minecraft/VOKAL");
            server.save(getActivity());

            server = new Server("Wow", "http://thedailyautist.com/map");
            server.save(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        View content = aInflater.inflate(R.layout.server_fragment, null);

        mList = (ListView) content.findViewById(R.id.list);

        mTouchListener = new SwipeDismissListViewTouchListener(mList, this);

        mList.setOnTouchListener(mTouchListener);
        mList.setOnScrollListener(mTouchListener.makeScrollListener());
    
        return content;
    }

    @Override
    public void onResume() {
        super.onResume();

        mNavManager = new ServerLoaderManager();
        getLoaderManager().initLoader(LOADER_SERVERS, null, mNavManager);
    }

    @Override
    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
        for (int position : reverseSortedPositions) {
        }
    }

    private class ServerLoaderManager implements LoaderManager.LoaderCallbacks<Cursor> {
        public Loader<Cursor> onCreateLoader(int aId, Bundle aArgs) {
            return new CursorLoader(getActivity(), Server.CONTENT_URI, Server.ALL,
                null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> aLoader, Cursor aData) {
            if (mList.getAdapter() == null) {
                ServerAdapter adapter = new ServerAdapter(getActivity(), aData);
                mList.setAdapter(adapter);
                mList.setOnItemClickListener(adapter);
            } else {
                ((ServerAdapter) mList.getAdapter()).swapCursor(aData);
            }
        }

        public void onLoaderReset(Loader<Cursor> aLoader) {
            ((ServerAdapter) mList.getAdapter()).swapCursor(null);
        }
    }

}
