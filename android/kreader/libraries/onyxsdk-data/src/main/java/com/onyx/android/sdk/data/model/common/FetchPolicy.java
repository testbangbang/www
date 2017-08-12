package com.onyx.android.sdk.data.model.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by suicheng on 2017/5/19.
 */

public class FetchPolicy {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CLOUD_ONLY, MEM_DB_ONLY, CLOUD_MEM_DB, MEM_DB_CLOUD, MEM_CLOUD_DB, DB_ONLY})
    public @interface Type {
    }

    public static final int CLOUD_ONLY = 0;
    public static final int MEM_DB_ONLY = 1;
    public static final int CLOUD_MEM_DB = 2;
    public static final int MEM_DB_CLOUD = 3;
    public static final int MEM_CLOUD_DB = 4;
    public static final int DB_ONLY = 5;

    public
    @Type
    static int translate(int val) {
        return val;
    }

    public static boolean isDataFromMemDb(@FetchPolicy.Type int policy, boolean wifiConnected) {
        if (policy == FetchPolicy.MEM_DB_ONLY) {
            return true;
        }
        if ((policy == FetchPolicy.CLOUD_MEM_DB ||
                policy == FetchPolicy.MEM_DB_CLOUD ||
                policy == FetchPolicy.MEM_CLOUD_DB) && !wifiConnected) {
            return true;
        }
        return false;
    }

    public static boolean isMemPartPolicy(@FetchPolicy.Type int fetchPolicy) {
        if (fetchPolicy == FetchPolicy.MEM_CLOUD_DB || fetchPolicy == FetchPolicy.MEM_DB_CLOUD ||
                fetchPolicy == FetchPolicy.MEM_DB_ONLY) {
            return true;
        }
        return false;
    }

    public static boolean isCloudPartPolicy(@FetchPolicy.Type int fetchPolicy) {
        if (fetchPolicy == FetchPolicy.CLOUD_ONLY || fetchPolicy == FetchPolicy.CLOUD_MEM_DB ||
                fetchPolicy == FetchPolicy.MEM_CLOUD_DB) {
            return true;
        }
        return false;
    }

    public static boolean isCloudOnlyPolicy(@FetchPolicy.Type int fetchPolicy) {
        return FetchPolicy.CLOUD_ONLY == fetchPolicy;
    }

    public static boolean isMemDbCloudPolicy(@FetchPolicy.Type int fetchPolicy) {
        return FetchPolicy.MEM_DB_CLOUD == fetchPolicy;
    }
}
