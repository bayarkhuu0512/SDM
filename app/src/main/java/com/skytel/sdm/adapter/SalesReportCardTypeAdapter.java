package com.skytel.sdp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.skytel.sdp.R;
import com.skytel.sdp.entities.CardType;

import java.util.List;

public class SalesReportCardTypeAdapter extends ArrayAdapter<CardType> {

    List<CardType> mList;
    Context context;

    public SalesReportCardTypeAdapter(Context context, int resource, List<CardType> objects) {
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
        View row = li.inflate(R.layout.sales_report_card_types_item, parent, false);
        TextView name = (TextView) row.findViewById(R.id.name);
        name.setText(mList.get(position).getDesciption() + "");

        return row;
    }

}
