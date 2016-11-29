package com.skytel.sdp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skytel.sdp.R;
import com.skytel.sdp.entities.HandsetChangeType;
import com.skytel.sdp.entities.VasType;

import java.util.List;

/**
 * Created by Altanchimeg on 7/8/2016.
 */

public class VasTypeAdapter   extends ArrayAdapter<VasType> {
    List<VasType> mList;
    Context context;

    public VasTypeAdapter(Context context, int resource, List<VasType> objects) {
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
        View row = li.inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
        TextView name = (TextView) row.findViewById(android.R.id.text1);
        name.setText(mList.get(position).getTypeName() + "");

        return row;
    }
}

