package com.onyx.android.dr.manager;

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

    private static final String LEAN_CLOUD_CACHE = "app_leanCloud";
    private static int RETRY_COUNT = 3;

    public static void initialize(Context context, String appId, String clientKey) {
        try {
            for (int i = 0; i < RETRY_COUNT; ++i) {
                if (initializeImpl(context, appId, clientKey)) {
                    return;
                }
            }
        } catch (Exception e) {
        }
    }

    public static boolean initializeImpl(Context context, String appId, String clientKey) {
        try {
            AVOSCloud.initialize(context, appId, clientKey);
            AVOSCloud.setLastModifyEnabled(isLastModifyEnabled);
            AVOSCloud.setDebugLogEnabled(isDebugLogEnabled);
            AVInstallation.getCurrentInstallation().saveInBackground();
            Log.i(TAG + "-installationId", getInstallationId());
        } catch (Exception e) {
            e.printStackTrace();
            cleanupLeanCloudCache(context);
            return false;
        }
        return true;
    }

    public static String getInstallationId() {
        return AVInstallation.getCurrentInstallation().getInstallationId();
    }

    private static boolean deleteDir(File dir) {
        if (dir == null) {
            return true;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
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
