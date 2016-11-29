package com.skytel.sdm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.skytel.sdm.R;
import com.skytel.sdm.entities.InfoNewsType;
import com.skytel.sdm.entities.NewsListItem;
import com.skytel.sdm.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Altanchimeg on 7/20/2016.
 */

public class NewsListAdapter extends BaseAdapter implements Constants {
    String TAG = NewsListAdapter.class.getName();
    private Context context;
    private List<NewsListItem> mList;
    private LayoutInflater mInflater;

    public NewsListAdapter(Context context, ArrayList<NewsListItem> list) {
        this.context = context;
        mList = list;

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refresh(ArrayList<NewsListItem> list) {
        mList = list;
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
            convertView = mInflater.inflate(R.layout.news_list_item, null);
            viewHolder.newsHeader = (TextView) convertView.findViewById(R.id.content_header);
            viewHolder.newsImage = (ImageView) convertView.findViewById(R.id.content_image);
            viewHolder.newsDate = (TextView) convertView.findViewById(R.id.content_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderContact) convertView.getTag();
        }
        viewHolder.newsHeader.setText(mList.get(position).getTitle());
        viewHolder.newsDate.setText(mList.get(position).getCreatedDate());
        Picasso.with(context).load(mList.get(position).getImage()).into(viewHolder.newsImage);
        convertView.setId(mList.get(position).getId());

        return convertView;
    }


    class ViewHolderContact {
        public TextView newsHeader;
        public ImageView newsImage;
        public TextView newsDate;
    }

}
