package com.onyx.android.sun.common;

import android.content.Context;
import android.widget.Toast;

import com.onyx.android.sun.SunApplication;

/**
 * Created by li on 2017/9/30.
 */

public class CommonNotices {
    public static void show(String message) {
        Toast.makeText(SunApplication.getInstence(), message, Toast.LENGTH_LONG).show();
    }
}
