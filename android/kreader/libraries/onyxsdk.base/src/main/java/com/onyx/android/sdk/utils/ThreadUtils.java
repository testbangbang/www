package com.onyx.android.sdk.utils;

/**
 * Created by zhuzeng on 12/9/15.
 */
public class ThreadUtils {

    static public void mySleep(int ms) {
        try {
            Thread.sleep(ms, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
