package com.skytel.sdm.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skytel.sdm.R;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.utils.Constants;

import java.util.List;

public class ChargeCardPackageTypeAdapter extends BaseAdapter implements Constants {
    String TAG = ChargeCardPackageTypeAdapter.class.getName();
    private Context mContext;
    private String[] mPackageTypes;
    private LayoutInflater mInflater;

    public ChargeCardPackageTypeAdapter(Context context) {
        this.mContext = context;
        mPackageTypes = mContext.getResources().getStringArray(R.array.skydealer_charge_card_types);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPackageTypes.length;
    }

    @Override
    public Object getItem(int position) {
        return mPackageTypes[position];
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
            convertView = mInflater.inflate(R.layout.charge_card_package_type_item, null);
            viewHolder.packageType = (TextView) convertView.findViewById(R.id.packageType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderContact) convertView.getTag();
        }
        viewHolder.packageType.setText(mPackageTypes[position]);
        return convertView;
    }

    class ViewHolderContact {
        public TextView packageType;
    }
}
