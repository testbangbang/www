package com.onyx.android.sdk.wifi;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2016/12/3 12:12.
 */

public class WifiBand {

    public static final int UNKNOWN = -1;
    //2.4ghz
    public static final int B_G_N_NETWORK = 0;
    //5ghz
    public static final int A_H_J_N_AC_NETWORK = 1;

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({UNKNOWN, B_G_N_NETWORK, A_H_J_N_AC_NETWORK})
    // Create an interface for validating int types
    public @interface WifiBandDef {
    }

    public
    @WifiBandDef
    static int translate(int val) {
        return val;
    }
}
