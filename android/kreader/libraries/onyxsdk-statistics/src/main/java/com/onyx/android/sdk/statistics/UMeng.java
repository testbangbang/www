package com.onyx.android.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 8/2/15.
 */
public class UMeng implements StatisticsBase {

    public boolean init(final Context context, final Map<String, String> args) {
        try {
            MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(context, args.get(KEY_TAG), args.get(CHANNEL_TAG));
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

    public void addHardwareInfo(final Map<String, String> map) {
        map.put("model", Build.MODEL);
        map.put("fp", Build.FINGERPRINT);
    }

    public void onDocumentOpenedEvent(final Context context, final String path, final String md5) {
        if (context == null) {
            return;
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("path", path);
        map.put("md5", md5);
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "documentOpen", map);
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

}
