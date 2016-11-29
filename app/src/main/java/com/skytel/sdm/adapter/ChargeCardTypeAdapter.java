package com.skytel.sdp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skytel.sdp.R;
import com.skytel.sdp.database.DataManager;
import com.skytel.sdp.entities.CardType;
import com.skytel.sdp.enums.PackageTypeEnum;
import com.skytel.sdp.utils.Constants;

import java.util.List;

public class ChargeCardTypeAdapter extends BaseAdapter implements Constants {
    String TAG = ChargeCardTypeAdapter.class.getName();
    private Context context;
    private DataManager dataManager;
    private List<CardType> mList;
    private LayoutInflater mInflater;

    public ChargeCardTypeAdapter(Context context, PackageTypeEnum packageTypeEnum) {
        this.context = context;
        dataManager = new DataManager(context);
        mList = dataManager.getCardTypeByPackageType(packageTypeEnum);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = mInflater.inflate(R.layout.charge_card_type_item, null);
            viewHolder.cardType = (TextView) convertView.findViewById(R.id.cardType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderContact) convertView.getTag();
        }
        viewHolder.cardType.setText(mList.get(position).getDesciption());
        convertView.setId(mList.get(position).getId());

        return convertView;
    }

    class ViewHolderContact {
        public TextView cardType;
    }

}
