package com.hanvon.core;

//javah -classpath ./build/intermediates/classes/debug/:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar:./com/hanvon/core -jni com.hanvon.core.Algorithm
public class Algorithm {

    static {
        try {
            System.loadLibrary("onyx_algorithm");
        } catch ( UnsatisfiedLinkError e ) {
            e.printStackTrace( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }

    public native static float distance(final float x1, final float y1, final float x2, final float y2, final float x, final float y);

}
