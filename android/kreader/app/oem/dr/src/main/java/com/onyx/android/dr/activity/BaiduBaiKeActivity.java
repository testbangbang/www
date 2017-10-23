package com.onyx.android.dr.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.reader.dialog.DialogDict;
import com.onyx.android.dr.reader.event.RedrawPageEvent;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;

/**
 * Created by huxiaomao on 17/9/25.
 */

public class BaiduBaiKeActivity extends BaseActivity {
    public static final String BAIDU_BAIKE_PARAM_KEY = "BaiduBaikeParam";
    @Bind(R.id.baidu_baike_content)
    WebView baiduBaikeContent;
    private String keyword;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_baidu_baike;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null) {
            CommonNotices.showMessage(this, getString(R.string.baidu_baike_parameter_error));
            finish();
            return;
        }
        keyword = intent.getStringExtra(BAIDU_BAIKE_PARAM_KEY);
        if (StringUtils.isNullOrEmpty(keyword)) {
            CommonNotices.showMessage(this, getString(R.string.baidu_baike_parameter_error));
            finish();
            return;
        }
        initWebView();
    }

    private void initWebView() {
        baiduBaikeContent.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings webSettings = baiduBaikeContent.getSettings();
        webSettings.setTextSize(WebSettings.TextSize.LARGER);
        baiduBaikeContent.loadUrl(DialogDict.BAIDU_BAIKE + keyword);
        baiduBaikeContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            EpdController.disableA2ForSpecificView(baiduBaikeContent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new RedrawPageEvent());
    }
}
