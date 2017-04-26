package com.onyx.kreader.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private static final String REABBLE_URL = "http://reabble.com/";

    @Bind(R.id.reabble_webView)
    WebView webView;

    private DoubleClickExitHelper doubleClickExitHelper;

    private static final int STORAGE_PHONE_PERMS_REQUEST_CODE = 1;
    private static final String[] STORAGE_PHONE_PERMS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        requestPermission();
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

    @AfterPermissionGranted(STORAGE_PHONE_PERMS_REQUEST_CODE)
    private void requestPermission() {
        String[] perms = STORAGE_PHONE_PERMS;
        if (EasyPermissions.hasPermissions(this, perms)) {

        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.request_permission_rationale),
                    STORAGE_PHONE_PERMS_REQUEST_CODE, perms);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PHONE_PERMS_REQUEST_CODE) {

        }
    }
}

