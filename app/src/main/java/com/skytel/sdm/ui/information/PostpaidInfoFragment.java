package com.skytel.sdm.ui.information;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.skytel.sdm.R;
import com.skytel.sdm.utils.Constants;

/**
 * Created by Altanchimeg on 7/21/2016.
 */

public class PostpaidInfoFragment extends Fragment implements Constants{
    private WebView mPostpaidInfoWebview;
    public PostpaidInfoFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.postpaid_info, container, false);

        mPostpaidInfoWebview = (WebView) rootView.findViewById(R.id.postpaid_info);
        mPostpaidInfoWebview.loadUrl(SERVER_SKYTEL_MN_URL+LINK_POSTPAID);

        WebSettings webSettings = mPostpaidInfoWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);


        return rootView;
    }

}