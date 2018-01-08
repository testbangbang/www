package com.onyx.jdread.shop.ui;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.DialogPayBinding;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.utils.WebViewInteraction;
import com.onyx.jdread.util.Utils;

/**
 * Created by li on 2018/1/6.
 */

public class PayFragment extends DialogFragment {
    private static final String TAG = PayFragment.class.getSimpleName();
    private DialogPayBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = (DialogPayBinding) DataBindingUtil.inflate(inflater, R.layout.dialog_pay_layout, container, false);
        initData();
        initView();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) (Utils.getScreenWidth(JDReadApplication.getInstance()) * 0.6);
        params.height = (int) (Utils.getScreenHeight(JDReadApplication.getInstance()) * 0.6);
        window.setAttributes(params);
    }

    private void initData() {
        Bundle arguments = getArguments();
        String url = arguments.getString(Constants.PAY_URL);
        binding.payWebView.loadUrl(url);
    }

    private void initView() {
        binding.payWebView.requestFocus();
        WebSettings settings = binding.payWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(Constants.APP_CACHE_MAX_SIZE);
        settings.setAppCachePath(Constants.LOCAL_WEB_CACHE_PATH);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);

        binding.payWebView.addJavascriptInterface(new WebViewInteraction(), WebViewInteraction.INTERACTION_NAME);
        binding.payWebView.removeJavascriptInterface("searchBoxJavaBridge_");
    }

    private void initListener() {
        binding.payWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                binding.payWebView.setFocusable(true);
                binding.payWebView.setFocusableInTouchMode(true);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Log.i(TAG, "errorCode:" + errorCode + ",description:" + description + ",failingUrl:" + failingUrl);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onDestroy() {
        if (binding.payWebView != null) {
            binding.payWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            binding.payWebView.clearHistory();

            ((ViewGroup) binding.payWebView.getParent()).removeView(binding.payWebView);
            binding.payWebView.destroy();
        }
        super.onDestroy();
    }
}
