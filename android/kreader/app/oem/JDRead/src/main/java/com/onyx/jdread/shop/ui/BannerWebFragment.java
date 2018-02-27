package com.onyx.jdread.shop.ui;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.jdread.R;
import com.onyx.jdread.databinding.FragmentBannerWebBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.model.TitleBarViewModel;
import com.onyx.jdread.shop.utils.WebViewInteraction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by li on 2018/1/6.
 */

public class BannerWebFragment extends BaseFragment {
    private static final String TAG = BannerWebFragment.class.getSimpleName();
    private FragmentBannerWebBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBannerWebBinding.inflate(inflater, container, false);
        initData();
        initView();
        initListener();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initLibrary();
    }

    private void initLibrary() {
        if (!getEventBus().isRegistered(this)) {
            getEventBus().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    private EventBus getEventBus() {
        return ShopDataBundle.getInstance().getEventBus();
    }

    private void initData() {
        String url = "";
        Bundle arguments = getBundle();
        if (arguments != null) {
            url = arguments.getString(Constants.BANNER_URL);
        }
        binding.bannerWebView.loadUrl(url);
    }

    private void initView() {
        binding.bannerWebView.requestFocus();
        WebSettings settings = binding.bannerWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(Constants.APP_CACHE_MAX_SIZE);
        settings.setAppCachePath(Constants.LOCAL_WEB_CACHE_PATH);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(false);

        binding.bannerWebView.addJavascriptInterface(new WebViewInteraction(), WebViewInteraction.INTERACTION_NAME);
        binding.bannerWebView.removeJavascriptInterface("searchBoxJavaBridge_");

        TitleBarViewModel titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.leftText = ResManager.getString(R.string.banner);
        titleBarViewModel.setEventBus(ShopDataBundle.getInstance().getEventBus());
        binding.setTitleBarViewModel(titleBarViewModel);
    }

    private void initListener() {
        binding.bannerWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                binding.bannerWebView.setFocusable(true);
                binding.bannerWebView.setFocusableInTouchMode(true);
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
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
        if (binding.bannerWebView != null) {
            binding.bannerWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            binding.bannerWebView.clearHistory();
            ((ViewGroup) binding.bannerWebView.getParent()).removeView(binding.bannerWebView);
            binding.bannerWebView.destroy();
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(TopBackEvent event) {
        if (getViewEventCallBack() != null) {
            getViewEventCallBack().viewBack();
        }
    }
}
