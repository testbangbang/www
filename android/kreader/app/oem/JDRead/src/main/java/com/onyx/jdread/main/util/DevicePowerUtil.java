package com.onyx.jdread.main.util;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by hehai on 18-2-2.
 */

public class DevicePowerUtil {

    public static void shutDownDevice(Context context) {
        try {
            Class<?> ServiceManager = Class.forName("android.os.ServiceManager");
            Method getService = ServiceManager.getMethod("getService", java.lang.String.class);
            Object oRemoteService = getService.invoke(null, Context.POWER_SERVICE);
            Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");
            Method asInterface = cStub.getMethod("asInterface", android.os.IBinder.class);
            Object oIPowerManager = asInterface.invoke(null, oRemoteService);
            Method shutdown = oIPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
            shutdown.invoke(oIPowerManager, false, true);
        } catch (Exception e) {
            Log.e("", e.toString(), e);
        }
    }
}
