package com.skytel.sdm.ui.skydealer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skytel.sdm.R;


public class SkymediaPaymentFragment extends Fragment {


    public SkymediaPaymentFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.skymedia_payment, container, false);
        return rootView;
    }

}
