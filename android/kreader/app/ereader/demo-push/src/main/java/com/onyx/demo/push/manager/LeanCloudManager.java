package com.onyx.demo.push.manager;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;

import java.io.File;

/**
 * Created by suicheng on 2017/3/3.
 */
public class LeanCloudManager {
    private static final String TAG = LeanCloudManager.class.getSimpleName();

    private static boolean isLastModifyEnabled = true;
    private static boolean isDebugLogEnabled = false;

    private final static String APP_ID = "M6M8NRsGaGk4gto7I5HUnUHA-gzGzoHsz";
    private final static String APP_KEY = "7LBuY74znzTeuJk11Kb3YBUL";

    private final static String FINGERPRINT_TAG = "FINGERPRINT";
    private final static String LANGUAGE_TAG = "lang";
    private final static String BUILD_ID_TAG = "bid";
    private static boolean inCheckingFirmware = false;
    private static final String LEAN_CLOUD_CACHE = "app_leanCloud";
    private static int RETRY_COUNT = 3;

    public static void initialize(Context context) {
        try {
            for (int i = 0; i < RETRY_COUNT; ++i) {
                if (initializeImpl(context)) {
                    return;
                }
            }
        } catch (Exception e) {
        }
    }

    public static boolean initializeImpl(Context context) {
        try {
            AVOSCloud.initialize(context, APP_ID, APP_KEY);
            AVOSCloud.setLastModifyEnabled(isLastModifyEnabled);
            AVOSCloud.setDebugLogEnabled(isDebugLogEnabled);
            AVInstallation.getCurrentInstallation().saveInBackground();
            Log.i(TAG + "-installationId", AVInstallation.getCurrentInstallation().getInstallationId());
        } catch (Exception exception) {
            exception.printStackTrace();
            cleanupLeanCloudCache(context);
            return false;
        }
        return true;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    static private void cleanupLeanCloudCache(final Context context) {
        File cache = context.getCacheDir();
        if (cache == null) {
            return;
        }
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            if (children == null) {
                return;
            }
            for (String s : children) {
                if (s.equals(LEAN_CLOUD_CACHE)) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }
}
