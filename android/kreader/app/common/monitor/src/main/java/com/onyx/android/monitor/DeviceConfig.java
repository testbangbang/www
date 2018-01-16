package com.onyx.android.monitor;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.RawResourceUtil;

/**
 * Created by suicheng on 2017/2/17.
 */
public class DeviceConfig {
    static private String TAG = DeviceConfig.class.getSimpleName();
    static private DeviceConfig globalInstance;
    private GObject backend;

    private final static String REFRESH_FREQUENCY = "refresh_frequency";

    static public DeviceConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new DeviceConfig(context);
        }
        return globalInstance;
    }


    private DeviceConfig(Context context) {
        backend = objectFromModel(context);
        if (backend != null) {
            Log.i(TAG, "Using device model.");
            return;
        }
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, name);
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

    public int getRefreshFrequency() {
        if (backend == null) {
            return 33;
        }
        return backend.getInt(REFRESH_FREQUENCY);
    }

}
