package com.onyx.android.sdk.utils;

import android.content.Context;
import android.os.Build;

import com.onyx.android.sdk.data.GObject;
import java.util.List;

/**
 * Created by jaky on 2017/6/28.
 */

public class LightConfig {

    static private String TAG = LightConfig.class.getSimpleName();

    static private LightConfig globalInstance;
    private GObject lightConfig;

    static public final String WARM_LIGHT_VALUE = "warm_light_value";
    static public final String COLD_LIGHT_VALUE = "cold_light_value";

    public static LightConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new LightConfig(context);
        }
        return globalInstance;
    }

    private LightConfig(Context context) {
        lightConfig = objectFromRawResource(context, Build.MODEL.toLowerCase());
        if (lightConfig != null) {
            return ;
        }
        lightConfig = objectFromRawResource(context, "brightness_config");
    }

    private GObject objectFromRawResource(Context context, final String name) {
        GObject object = null;
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            object = RawResourceUtil.objectFromRawResource(context, res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return object;
        }
    }

    public Integer[] getWarmLightValues() {
        if (lightConfig.hasKey(WARM_LIGHT_VALUE)) {
            List<Integer> list = lightConfig.getList(WARM_LIGHT_VALUE);
            return list.toArray(new Integer[list.size()]);
        }
        return new Integer[]{0,3,6,9,12,15,17,19,21,23,25,26,27,28,29,30,31};
    }

    public Integer[] getColdLightValues() {
        if (lightConfig.hasKey(COLD_LIGHT_VALUE)) {
            List<Integer> list = lightConfig.getList(COLD_LIGHT_VALUE);
            return list.toArray(new Integer[list.size()]);
        }
        return new Integer[]{0, 3, 6, 9, 12, 15, 17, 19, 21, 23, 25, 26, 27, 28, 29, 30, 31};
    }

    public Integer[][] getNaturalLightValues() {
        Integer[][] lightValues =new Integer[2][];
        lightValues[0] = getColdLightValues();
        lightValues[1] = getWarmLightValues();
        return lightValues;
    }
}
