package com.onyx.android.sdk.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.ViewTreeObserver;

/**
 * Created by Joy on 2016/5/24.
 */
public class TreeObserverUtils {
    @SuppressWarnings("deprecation")
    public static void removeLayoutListenerPre16(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener){
        observer.removeGlobalOnLayoutListener(listener);
    }

    @TargetApi(16)
    public static void removeLayoutListenerPost16(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener){
        observer.removeOnGlobalLayoutListener(listener);
    }

    public static void removeGlobalOnLayoutListener(final ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            TreeObserverUtils.removeLayoutListenerPre16(observer, listener);
        } else {
            TreeObserverUtils.removeLayoutListenerPost16(observer, listener);
        }
    }

}
