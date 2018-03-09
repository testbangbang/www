package com.evernote.client.android;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.evernote.androidsdk.R;
import com.evernote.client.android.helper.Cat;
import com.evernote.client.android.login.EvernoteLoginTask;

/**
 * Created by li on 2018/3/5.
 */

public class EvernoteOAuthDialog extends DialogFragment {
    private static final Cat CAT = new Cat("EvernoteOAuthDialog");
    private View view;
    private WebView webView;
    private String url;
    private static final String HOST_EVERNOTE = "www.evernote.com";
    private static final String HOST_SANDBOX = "sandbox.evernote.com";
    private static final String HOST_CHINA = "app.yinxiang.com";
    private EvernoteLoginTask.LoginTaskCallback callback;
    private ImageView close;

    public void setCallback(EvernoteLoginTask.LoginTaskCallback callback, String url) {
        this.callback = callback;
        this.url = url;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        view = inflater.inflate(R.layout.esdk__dialog_login_layout, container, false);
        initData();
        initView();
        initListener();
        return view;
    }

    private void initListener() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initData() {
        setResultUri(null);
        if (TextUtils.isEmpty(url)) {
            CAT.w("no uri passed, return cancelled");
            dismiss();
            return;
        }

        Uri uri = Uri.parse(url);
        if (!"https".equalsIgnoreCase(uri.getScheme())) {
            CAT.w("https required, return cancelled");
            dismiss();
            return;
        }

        String host = uri.getHost();
        if (!HOST_EVERNOTE.equalsIgnoreCase(host) && !HOST_SANDBOX.equalsIgnoreCase(host) && !HOST_CHINA.equalsIgnoreCase(host)) {
            CAT.w("unacceptable host, return cancelled");
            dismiss();
            return;
        }
    }

    private void initView() {
        webView = (WebView) view.findViewById(R.id.webview);
        close = (ImageView)view.findViewById(R.id.dialog_login_close);

        webView.setWebViewClient(mWebViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (EvernoteOAuthHelper.CALLBACK_SCHEME.equals(uri.getScheme())) {
                setResultUri(url);
                dismiss();
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    };

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        android.view.WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.height = (int) (getScreenHeight(getActivity()) * 0.8);
        layoutParams.width = (int) (getScreenWidth(getActivity()) * 0.8);
        window.setAttributes(layoutParams);
        super.onResume();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        setResultUri(url);
        destroyWebView();
        super.onDismiss(dialog);
    }

    private void destroyWebView() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }

    private void setResultUri(String clickedOAuthUri) {
        Intent data = new Intent();
        data.putExtra(EvernoteUtil.EXTRA_OAUTH_CALLBACK_URL, clickedOAuthUri);
        if (callback != null) {
            callback.onDismiss(EvernoteLoginTask.REQUEST_AUTH, TextUtils.isEmpty(clickedOAuthUri) ? 0 : -1, data);
        }
    }

    private DisplayMetrics getDisplayMetrics(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    private int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    private int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }
}
