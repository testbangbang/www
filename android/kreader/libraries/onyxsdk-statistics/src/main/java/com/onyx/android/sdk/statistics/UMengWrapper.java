package com.onyx.android.sdk.statistics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 8/2/15.
 */
public class UMengWrapper {

    static private boolean useUMeng;

    public static void init(boolean use, final Context context, final String id) {
        try {
            useUMeng = use;
            MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
            MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(context, id, "normal");
            MobclickAgent.startWithConfigure(config);
        } catch (Exception e) {
        }
    }

    public static void activityResume(final Context context) {
        if (!useUMeng) {
            return;
        }
        MobclickAgent.onResume(context);
    }

    public static void activityPause(final Context context) {
        if (!useUMeng || context == null) {
            return;
        }
        MobclickAgent.onPause(context);
    }

    public static void addHardwareInfo(final Map<String, String> map) {
        map.put("model", Build.MODEL);
        map.put("fp", Build.FINGERPRINT);
    }


    public static void collectViewDocumentEvent(final Context context, final String path) {
        if (!useUMeng || context == null) {
            return;
        }

        Map<String,String> map = new HashMap<String,String>();
        map.put("path", path);
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "viewDocument", map);
    }

    public static void collectActivityStartEvent(final Context context, final Intent intent) {
        if (!useUMeng || context == null) {
            return;
        }
        Map<String,String> map = new HashMap<String,String>();
        map.put("pkg", intent.getComponent().flattenToString());
        addHardwareInfo(map);
        MobclickAgent.onEvent(context, "startApp", map);
    }
}
