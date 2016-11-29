package com.skytel.sdp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.skytel.sdp.MainActivity;
import com.skytel.sdp.R;

/**
 * Created by Altanchimeg on 4/16/2016.
 */
public class LeftMenuListAdapter extends BaseAdapter {
    String TAG = LeftMenuListAdapter.class.getName();
    private Context mContext;
    private String[] mMenus;
    private String[] mIcons;
    private LayoutInflater mLayoutInflater = null;

    public LeftMenuListAdapter(Context context) {
        mContext = context;
        mMenus = context.getResources().getStringArray(R.array.leftmenu_array);
        mIcons = context.getResources().getStringArray(R.array.leftmenu_ic_array);
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return mMenus.length;
    }

    @Override
    public Object getItem(int position) {
        return mMenus[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CompleteListViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.left_menu_item, null);
            viewHolder = new CompleteListViewHolder(v);
            v.setTag(viewHolder);
        } else {
            viewHolder = (CompleteListViewHolder) v.getTag();
        }

        viewHolder.leftMenuName.setText(mMenus[position] + "");
        viewHolder.leftMenuName.setTextColor(mContext.getResources().getColor(R.color.colorMenuText));
        Log.d(TAG, "MainActivity.currentMenu " + MainActivity.sCurrentMenu);
        Log.d(TAG, "position " + position);
        Drawable d = null;
        if (MainActivity.sCurrentMenu == position) {
            viewHolder.leftMenuItemContainer.setBackgroundColor(mContext.getResources().getColor(R.color.colorMenuBackgroundSelected));
            d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier(mIcons[position] + "_light", "drawable", mContext.getPackageName()));
        } else {
            viewHolder.leftMenuItemContainer.setBackgroundColor(mContext.getResources().getColor(R.color.colorBackground));
            d = mContext.getResources().getDrawable(mContext.getResources().getIdentifier(mIcons[position], "drawable", mContext.getPackageName()));
        }
        try {
            viewHolder.leftMenuIcon.setBackground(d);
        } catch (Exception e) {
        }


        return v;

    }

    class CompleteListViewHolder {
        public TextView leftMenuName;
        public ImageView leftMenuIcon;
        public LinearLayout leftMenuItemContainer;

        public CompleteListViewHolder(View base) {
            leftMenuName = (TextView) base.findViewById(R.id.leftMenuName);
            leftMenuIcon = (ImageView) base.findViewById(R.id.leftMenuIcon);
            leftMenuItemContainer = (LinearLayout) base.findViewById(R.id.leftMenuItemContainer);
        }
    }
}
