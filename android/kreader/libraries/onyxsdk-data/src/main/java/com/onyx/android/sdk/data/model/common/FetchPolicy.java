package com.onyx.android.sdk.data.model.common;

import android.support.annotation.IntDef;

import com.onyx.android.sdk.utils.NetworkUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by suicheng on 2017/5/19.
 */

public class FetchPolicy {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CLOUD_ONLY, LOCAL_ONLY, CLOUD_LOCAL, LOCAL_CLOUD})
    public @interface Type {
    }

    public static final int CLOUD_ONLY = 0;
    public static final int LOCAL_ONLY = 1;
    public static final int CLOUD_LOCAL = 2;
    public static final int LOCAL_CLOUD = 3;

    public
    @Type
    static int translate(int val) {
        return val;
    }

    public static boolean isDataFromLocal(@FetchPolicy.Type int policy, boolean wifiConnected) {
        if (policy == FetchPolicy.LOCAL_ONLY) {
            return true;
        }
        if (policy == FetchPolicy.CLOUD_LOCAL && !wifiConnected) {
            return true;
        }
        if (policy == FetchPolicy.LOCAL_CLOUD && !wifiConnected) {
            return true;
        }
        return false;
    }
}
