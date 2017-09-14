package com.onyx.edu.teacher;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.edu.teacher.databinding.ActivityMainBinding;
import com.onyx.edu.teacher.termux.Constant;
import com.onyx.edu.teacher.termux.TermuxInstaller;
import com.onyx.edu.teacher.termux.TermuxSession;

/**
 * Created by lxm on 2017/9/11.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    // use root
    private boolean failSafe = false;
    private TermuxSession session;
    private boolean startWebService = false;
    private boolean openWeb = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = (ActivityMainBinding) DataBindingUtil.setContentView(this, R.layout.activity_main);
        initWebView(binding.content);
        installTermux();
    }

    private void installTermux() {
        TermuxInstaller.setupIfNeeded(this, new Runnable() {
            @Override
            public void run() {
                initTermuxSession();
            }
        });
    }

    private void initTermuxSession() {
        resetState();
        String executablePath = (failSafe ? Constant.ROOT_SYSTEM_SH_PATH : null);
        session = TermuxSession.createTermSession(executablePath, null, null, failSafe, new TermuxSession.Callback() {
            @Override
            public void onMessageReceived(String text, String action) {
                if (action.equals(Constant.COMMAND_LOGIN_SHELL_ACTION)) {
                    startWebService();
                }
                if (text.contains(Constant.START_WEB_SERVER_SUCCESS_EVENT)) {
                    openWeb();
                }
            }
        });
    }

    private void resetState() {
        startWebService = false;
        openWeb = false;
    }

    private void startWebService() {
        if (session == null || startWebService) {
            return;
        }
        startWebService = true;
        session.write(Constant.START_WEB_SERVER_COMMAND, Constant.COMMAND_START_WEB_ACTION);

    }

    private void openWeb() {
        if (openWeb) {
            return;
        }
        openWeb =  true;
        binding.content.loadUrl(Constant.WEB_URL);
    }

    private void initWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

}
