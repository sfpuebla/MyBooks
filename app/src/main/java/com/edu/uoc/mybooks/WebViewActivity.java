package com.edu.uoc.mybooks;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

public class WebViewActivity extends Fragment {
     WebView web_view;
    View rootview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.activity_web_view , container, false);
         web_view = (WebView) rootview.findViewById(R.id.web_view );
        return rootview;
    }




}
