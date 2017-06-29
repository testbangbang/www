package com.onyx.android.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.onyx.android.sdk.data.model.DocumentInfo;
import com.onyx.android.sdk.utils.StringUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 8/2/15.
 */
public class UMeng implements StatisticsBase {

    public boolean init(final Context context, final Map<String, Object> args) {
        try {
            MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(context, (String) args.get(KEY_TAG), (String) args.get(CHANNEL_TAG));
            MobclickAgent.startWithConfigure(config);
            MobclickAgent.setDebugMode(true);

        } catch (Exception e) {
        }
        return true;
    }

    public void onActivityResume(final Context context) {
        MobclickAgent.onResume(context);
    }

    public void onActivityPause(final Context context) {
        if (context == null) {
            return;
        }
        MobclickAgent.onPause(context);
    }

    @Override
    public void onNetworkChanged(Context context, boolean connected, int networkType) {

    }

    public void addHardwareInfo(final Map<String, String> map) {
        map.put("model", Build.MODEL);
        map.put("fp", Build.FINGERPRINT);
    }

    public void onDocumentOpenedEvent(final Context context, final DocumentInfo documentInfo) {
        if (context == null) {
            return;
        }

        MobclickAgent.onResume(context);
        Map<String,String> map = new HashMap<String,String>();
        map.put("path", documentInfo.getPath());
        map.put("md5", documentInfo.getMd5());
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "documentOpen", map);
    }

    public void onDocumentClosed(final Context context) {
        MobclickAgent.onPause(context);
    }

    public void onPageChangedEvent(final Context context, final String last, final String current, long duration) {
        if (context == null) {
            return;
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("last", last);
        map.put("current", current);
        map.put("duration", String.valueOf(duration));
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "pageChange", map);
    }

    public void onDocumentFinished(final Context context, final String comment, final int score) {

    }

    public void onTextSelectedEvent(final Context context, final String text) {
        if (context == null) {
            return;
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("text", text);
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "textSelect", map);
    }

    public void onAddAnnotationEvent(final Context context, final String originText, final String userNote) {
        if (context == null) {
            return;
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("originText", originText);
        map.put("userNote", userNote);
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "addAnnotation", map);
    }

    public void onDictionaryLookupEvent(final Context context, final String originText) {
        if (context == null) {
            return;
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("originText", originText);
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "dictLookup", map);
    }

    @Override
    public void onBatteryStatusChange(Context context, String status, int level) {

    }

    @Override
    public void onFormFieldSelected(Context context, String formId, String value) {

    }
}
