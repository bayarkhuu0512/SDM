package com.skytel.sdp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skytel.sdp.R;
import com.skytel.sdp.entities.Phonenumber;

import java.util.List;

public class NumberChoiceAdapter extends BaseAdapter {
    String TAG = NumberChoiceAdapter.class.getName();

    private Context mContext;
    private List<Phonenumber> mList;

    public NumberChoiceAdapter(Context context, List<Phonenumber> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.numberchoice_cell, null);
            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }
        viewHolder.mPhoneNumber.setText(mList.get(position).getPhoneNumber() + "");


        return v;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}

class ViewHolder {
    public TextView mPhoneNumber;

    public ViewHolder(View base) {
        mPhoneNumber = (TextView) base.findViewById(R.id.phone_number);
    }
}
