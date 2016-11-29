package com.skytel.sdp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.skytel.sdp.R;
import com.skytel.sdp.entities.InfoNewsType;
import com.skytel.sdp.ui.information.InfoNewsFragment;
import com.skytel.sdp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Altanchimeg on 7/19/2016.
 */

public class InfoNewsTypeAdapter extends BaseAdapter implements Constants {
    String TAG = InfoNewsTypeAdapter.class.getName();
    private Context context;
    private List<InfoNewsType> mList;
    private LayoutInflater mInflater;
    private int currentCategoryPos = 0;

    public InfoNewsTypeAdapter(Context context, ArrayList<InfoNewsType> list) {
        this.context = context;
        mList = list;
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
            convertView = mInflater.inflate(R.layout.info_news_type_item, null);
            viewHolder.infoNews = (TextView) convertView.findViewById(R.id.infoNewsType);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderContact) convertView.getTag();
        }
        viewHolder.infoNews.setText(mList.get(position).getName());
        convertView.setId(mList.get(position).getId());

        if (currentCategoryPos == position) {
            viewHolder.infoNews.setBackgroundColor(context.getResources().getColor(R.color.colorSkytelYellow));
            viewHolder.infoNews.setTextColor(Color.WHITE);
        } else {
            viewHolder.infoNews.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            viewHolder.infoNews.setTextColor(context.getResources().getColor(R.color.colorMenuText));

        }

        return convertView;
    }

    public void setCurrentCategoryPos(int pos) {
        currentCategoryPos = pos;
    }

    class ViewHolderContact {
        public TextView infoNews;
    }

}
