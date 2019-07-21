package com.github.fredrik9000.firmadetaljer_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomepageFragment extends Fragment {

    public static final String ARG_URL = "url";
    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString(ARG_URL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        WebView webView = new WebView(getContext());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(addHTTPIfNotPresent(url));
        return webView;
    }

    private String addHTTPIfNotPresent(String url)  {
        if (url.contains("https://") || url.contains("http://")) {
            return url;
        } else {
            return "http://" + url;
        }
    }

}
