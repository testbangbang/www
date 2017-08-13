package com.onyx.android.sample.utils;

/**
 * Created by john on 13/8/2017.
 */

public class EpdUtils {

    static {
        try {
            System.loadLibrary("onyx_epd_update");
        } catch ( UnsatisfiedLinkError e ) {
            e.printStackTrace( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

    public native static void test();

}
