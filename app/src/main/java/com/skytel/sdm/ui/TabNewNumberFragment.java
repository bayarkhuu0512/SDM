package com.skytel.sdm.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.skytel.sdm.R;
import com.skytel.sdm.ui.newnumber.NumberChoiceFragment;
import com.skytel.sdm.ui.newnumber.NumberOrderReportFragment;

public class TabNewNumberFragment extends Fragment {

    FragmentPagerAdapter mFragmentPagerAdapter;
    ViewPager mViewPager;

    public TabNewNumberFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tab_pager, container, false);

       /* Toast.makeText(getActivity(), getText(R.string.tab_newnumber_order)+" хэсэг нь энэхүү хувилбарт ажиллахгүй байгаа. ", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Та хувилбарт багтсан хэсгийг сонгон тестэлнэ үү. Баярлалаа", Toast.LENGTH_SHORT).show();
*/
        mFragmentPagerAdapter = new FragmentPagerAdapter(getActivity().getFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager_view);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_view);
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.tab_newnumber_order)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.tab_order_report)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        return rootView;
    }

    public static class FragmentPagerAdapter extends FragmentStatePagerAdapter {

        public FragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            Fragment fragment;
            if (i == 0) {
                fragment = new NumberChoiceFragment();
            } else {
                fragment = new NumberOrderReportFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + (position + 1);
        }
    }

}
