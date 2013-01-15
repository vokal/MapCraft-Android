package com.vokal.mapcraft.widget;

import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.List;

import com.vokal.mapcraft.R;
import com.vokal.mapcraft.models.*;

public class TileSetNavAdapter extends ArrayAdapter<TileSet> {

    LayoutInflater mInflater;
    int mSeparatorIndex;
    
    public TileSetNavAdapter(Context aContext, List<TileSet> aList, int aSeparatorIndex) {
        super(aContext, R.id.name, aList);
        mSeparatorIndex = aSeparatorIndex;

        mInflater = LayoutInflater.from(aContext);
    }

    @Override
    public View getDropDownView(int aPos, View aConvert, ViewGroup aParent) {
        View result = aConvert;
        if (result == null) {
            result = mInflater.inflate(R.layout.nav_item, null);
        }

        ImageView icon = (ImageView) result.findViewById(R.id.icon);
        TextView name  = (TextView) result.findViewById(R.id.name);

        TileSet t = getItem(aPos);
        
        if (aPos < mSeparatorIndex) {
            name.setText(t.getWorldName());
            icon.setImageResource(R.drawable.ic_action_world);
        } else {
            name.setText(t.getName());
            icon.setImageResource(R.drawable.ic_action_tiles);
        }

        return result;
    }

    @Override
    public View getView(int aPos, View aConvert, ViewGroup aParent) {
        View result = aConvert;
        if (result == null) {
            result = mInflater.inflate(R.layout.nav_item, null);
        }

        ImageView icon = (ImageView) result.findViewById(R.id.icon);
        TextView name  = (TextView) result.findViewById(R.id.name);

        TileSet t = getItem(aPos);
        
        name.setText(t.getName());
        icon.setImageResource(R.drawable.ic_action_tiles);

        return result;
    }
}
