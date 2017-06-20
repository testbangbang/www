package com.onyx.kreader.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.kreader.R;
import com.onyx.kreader.utils.DoubleClickExitHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String REABBLE_URL = "http://reabble.com/";

    @Bind(R.id.reabble_webView)
    WebView webView;

    private DoubleClickExitHelper doubleClickExitHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        initConfig();
        initView();
        initData();
    }

    private int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initConfig() {
        doubleClickExitHelper = new DoubleClickExitHelper(this);
    }

    private void initView() {
        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
    }

    private void initData() {
        webView.loadUrl(REABBLE_URL);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return doubleClickExitHelper.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

