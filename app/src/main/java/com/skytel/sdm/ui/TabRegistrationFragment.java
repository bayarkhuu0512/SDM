package com.skytel.sdp.ui;


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

import com.skytel.sdp.R;
import com.skytel.sdp.ui.registration.DealerRegistrationFragment;
import com.skytel.sdp.ui.registration.RegistrationReportFragment;
import com.skytel.sdp.ui.registration.SkyMediaRegistrationFragment;
import com.skytel.sdp.ui.skydealer.ChargeCardFragment;
import com.skytel.sdp.ui.skydealer.PostPaidPaymentFragment;
import com.skytel.sdp.ui.skydealer.SalesReportFragment;
import com.skytel.sdp.ui.skydealer.SkymediaPaymentFragment;

public class TabRegistrationFragment extends Fragment {

    FragmentPagerAdapter mFragmentPagerAdapter;
    ViewPager mViewPager;

    public TabRegistrationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tab_pager, container, false);
/*
        Toast.makeText(getActivity(), getText(R.string.registration)+" хэсэг нь энэхүү хувилбарт ажиллахгүй байгаа. ", Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Та хувилбарт багтсан хэсгийг сонгон тестэлнэ үү. Баярлалаа", Toast.LENGTH_SHORT).show();
*/
        mFragmentPagerAdapter = new FragmentPagerAdapter(getActivity().getFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager_view);
        mViewPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tab_view);
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.tab_registration_dealer)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.tab_registration_skymedia)));
        tabLayout.addTab(tabLayout.newTab().setText(getText(R.string.tab_registration_report)));

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
                fragment = new DealerRegistrationFragment();
            } else if(i==1){
                fragment = new SkyMediaRegistrationFragment();
            } else {
                fragment = new RegistrationReportFragment();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + (position + 1);
        }
    }

}
