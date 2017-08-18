package com.onyx.phone.reader.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.phone.reader.R;
import com.onyx.phone.reader.action.AppUpdateCheckAction;
import com.onyx.phone.reader.manager.PermissionManager;
import com.onyx.phone.reader.reader.data.ReaderDataHolder;
import com.onyx.phone.reader.utils.DoubleClickExitHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
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
        checkPermission();
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
    private void checkPermission() {
        String[] perms = STORAGE_PHONE_PERMS;
        if (EasyPermissions.hasPermissions(this, perms)) {
            afterPermissionGranted();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.request_permission_rationale),
                    STORAGE_PHONE_PERMS_REQUEST_CODE, perms);
        }
    }

    private void afterPermissionGranted() {
        checkAppUpdate();
    }

    private void checkAppUpdate() {
        AppUpdateCheckAction updateCheckAction = new AppUpdateCheckAction();
        updateCheckAction.execute(new ReaderDataHolder(this), null);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        PermissionManager.processPermissionPermanentlyDenied(this, getString(R.string.tip_of_permissions_request),
                requestCode, perms);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}

