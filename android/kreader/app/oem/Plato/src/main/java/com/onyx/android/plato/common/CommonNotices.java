package com.onyx.android.plato.common;

import android.widget.Toast;

import com.onyx.android.plato.SunApplication;

/**
 * Created by li on 2017/9/30.
 */

public class CommonNotices {
    public static void show(String message) {
        Toast.makeText(SunApplication.getInstance(), message, Toast.LENGTH_LONG).show();
    }
}
