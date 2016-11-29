package com.skytel.sdm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skytel.sdm.R;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.entities.DealerChannelType;

import java.util.List;

/**
 * Created by Altanchimeg on 7/1/2016.
 */

public class DealerChannelTypeAdapter extends ArrayAdapter<DealerChannelType> {
    List<DealerChannelType> mList;
    Context context;

    public DealerChannelTypeAdapter(Context context, int resource, List<DealerChannelType> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mList = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = li.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        TextView name = (TextView) row.findViewById(android.R.id.text1);
        name.setText(mList.get(position).getTypeName() + "");

        return row;
    }

}
