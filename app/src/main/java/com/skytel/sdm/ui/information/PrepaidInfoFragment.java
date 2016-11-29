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
 * Created by Altanchimeg on 7/18/2016.
 */

public class PrepaidInfoFragment extends Fragment implements Constants{
    private WebView mPrepaidInfoWebview;
    public PrepaidInfoFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.prepaid_info, container, false);

        mPrepaidInfoWebview = (WebView) rootView.findViewById(R.id.prepaid_info);
        mPrepaidInfoWebview.loadUrl(SERVER_SKYTEL_MN_URL+LINK_PREPAID);

        WebSettings webSettings = mPrepaidInfoWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        return rootView;
    }

}