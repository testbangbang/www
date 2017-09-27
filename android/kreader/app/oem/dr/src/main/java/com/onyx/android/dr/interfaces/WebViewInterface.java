package com.onyx.android.dr.interfaces;

import android.webkit.JavascriptInterface;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.event.WebViewJSEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/9/26.
 */

public class WebViewInterface {
    public static final String INTERFACE_NAME = "control";

    @JavascriptInterface
    public void showDialog(String message) {
        EventBus.getDefault().post(new WebViewJSEvent(message));
    }
}
