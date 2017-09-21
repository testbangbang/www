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
    private AtomicInteger wakeLockCounting = new AtomicInteger(0);
    public final static int FULL_FLAGS = PowerManager.FULL_WAKE_LOCK;
    public final static int ON_AFTER_RELEASE = PowerManager.ON_AFTER_RELEASE;
    public final static int WAKEUP_FLAGS = PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP;
    private volatile boolean referenceCounted = true;

    public WakeLockHolder() {
    }

    public WakeLockHolder(boolean ref) {
        referenceCounted = ref;
    }

    public synchronized void acquireWakeLock(final Context context, final String tag) {
        acquireWakeLock(context, FULL_FLAGS, tag, -1);
    }

    public synchronized void acquireWakeLock(final Context context, int flags, final String tag) {
        acquireWakeLock(context, flags, tag, -1);
    }

    public synchronized void acquireWakeLock(final Context context, int flags, final String tag, int ms) {
        try {
            if (wakeLock == null) {
                wakeLock = Device.currentDevice().newWakeLockWithFlags(context, flags, tag);
                wakeLock.setReferenceCounted(referenceCounted);
            }
            if (wakeLock != null) {
                if (ms > 0) {
                    wakeLock.acquire(ms);
                } else {
                    wakeLock.acquire();
                }
                if (referenceCounted) {
                    wakeLockCounting.incrementAndGet();
                } else {
                    wakeLockCounting.set(1);
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void releaseWakeLock() {
        try {
            if (wakeLock != null) {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }

                wakeLockCounting.decrementAndGet();
                if (wakeLockCounting.get() <= 0 || !referenceCounted) {
                    wakeLockCounting.set(0);
                    wakeLock = null;
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void forceReleaseWakeLock() {
        if (wakeLock != null) {
            if (wakeLock.isHeld()) {
                wakeLock.setReferenceCounted(false);
                wakeLock.release();
            }
            wakeLock = null;
            wakeLockCounting.set(0);
        }
    }

    public synchronized void dumpWakelocks(final String tag) {
        if ((wakeLock != null && wakeLockCounting.get() <= 0) ||
            (wakeLock == null && wakeLockCounting.get() > 0)) {
            Log.e(tag, " Counting unmatched!");
            return;
        }
        if (wakeLock != null || wakeLockCounting.get() > 0) {
            Log.e(tag, "Wake lock in using: " + wakeLock.toString() + " counting: " + wakeLockCounting.get());
        } else {
            Log.e(tag, "Wake lock released.");
        }
    }


}
