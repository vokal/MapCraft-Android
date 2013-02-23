package com.vokal.mapcraft.widget;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.view.*;
import android.widget.*;

import android.support.v4.app.*;
import android.support.v4.widget.CursorAdapter;

import com.vokal.mapcraft.R;
import com.vokal.mapcraft.models.Server;

public class ServerAdapter extends CursorAdapter {
    private static final String TAG = "ServerAdapter";

    private Context aContext;
    private LayoutInflater mInflater;

    public ServerAdapter(Context aContext, Cursor aCursor) {
        super(aContext, aCursor, false);

        mContext = aContext;
        mInflater = LayoutInflater.from(aContext);
    }

    @Override
    public View newView(Context aContext, Cursor aCursor, ViewGroup aParent) {
        View result = mInflater.inflate(R.layout.server_item, null);
        result.setTag(new ViewHolder(result));
        return result;
    }

    @Override
    public void bindView(View aView, Context aContext, Cursor aCursor) {
        
        Server server = Server.fromCursor(aCursor);

        ViewHolder holder = (ViewHolder) aView.getTag();
        holder.title.setText(server.getName());
    }

    static class ViewHolder {
        protected TextView title;
        protected ImageView preview;

        ViewHolder(View aView) { 
            title   = (TextView) aView.findViewById(R.id.title);
            preview = (ImageView) aView.findViewById(R.id.preview);
        }
    }
}
