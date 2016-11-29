package com.skytel.sdm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skytel.sdm.R;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.entities.PriceType;
import com.skytel.sdm.utils.Constants;

import java.util.List;

public class PriceTypeInfoListAdapter extends BaseAdapter implements Constants {
    String TAG = PriceTypeInfoListAdapter.class.getName();
    private Context mContext;
    List<PriceType> mList;
    private LayoutInflater mInflater;

    public PriceTypeInfoListAdapter(Context context, List<PriceType> priceTypes) {
        mContext = context;
        mList = priceTypes;
        mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderContact viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolderContact();
            convertView = mInflater.inflate(R.layout.newnumber_pricetypeinfo_item, null);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.info = (TextView) convertView.findViewById(R.id.info);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderContact) convertView.getTag();
        }
        viewHolder.price.setText(mList.get(position).getPrice()+"₮ ");
        viewHolder.info.setText(mList.get(position).getUnit()+"нэгж  "+mList.get(position).getDays()+"хоногтой");
        return convertView;
    }

    class ViewHolderContact {
        public TextView price;
        public TextView info;
    }
}
