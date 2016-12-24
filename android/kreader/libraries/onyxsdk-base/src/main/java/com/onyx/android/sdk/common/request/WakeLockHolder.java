package com.onyx.android.sdk.common.request;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.onyx.android.sdk.device.Device;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhuzeng on 23/12/2016.
 */

public class WakeLockHolder {

    private volatile PowerManager.WakeLock wakeLock;
    private AtomicInteger wakeLockCounting = new AtomicInteger();

    public void acquireWakeLock(final Context context, final String tag) {
        acquireWakeLockWithTimeout(context, tag, -1);
    }

    public void acquireWakeLockWithTimeout(final Context context, final String tag, int ms) {
        try {
            if (wakeLock == null) {
                wakeLock = Device.currentDevice().newWakeLock(context, tag);
            }
            if (wakeLock != null) {
                if (ms > 0) {
                    wakeLock.acquire(ms);
                } else {
                    wakeLock.acquire();
                }
                wakeLockCounting.incrementAndGet();
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }



    public void releaseWakeLock() {
        try {
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                if (wakeLockCounting.decrementAndGet() <= 0) {
                    wakeLock = null;
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public void dumpWakelocks(final String tag) {
        if (wakeLock != null || wakeLockCounting.get() > 0) {
            Log.w(tag, "wake lock not released. check wake lock." + wakeLock.toString() + " counting: " + wakeLockCounting.get());
        }
    }


}
