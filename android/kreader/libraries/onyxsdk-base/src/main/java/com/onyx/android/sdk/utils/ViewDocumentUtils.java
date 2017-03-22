package com.onyx.android.sdk.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by suicheng on 2016/12/5.
 */
public class ViewDocumentUtils {

    public static String ACTION_ENABLE_READER_DEBUG_LOG = "com.onyx.android.sdk.action_enable_reader_debug_log";
    public static String ACTION_DISABLE_READER_DEBUG_LOG = "com.onyx.android.sdk.action_disable_reader_debug_log";

    public static String TAG_AUTO_SLIDE_SHOW_MODE = "auto_slide_show";
    public static String TAG_SLIDE_SHOW_MAX_PAGE_COUNT = "slide_show_page_count";
    public static String TAG_SLIDE_SHOW_INTERVAL_IN_SECONDS = "slide_show_mode";

    public static Intent viewActionIntent(final File file) {
        final Intent intent = new Intent();
        intent.setData(Uri.fromFile(file));
        intent.setAction(Intent.ACTION_VIEW);
        return intent;
    }

    public static Intent autoSlideShowIntent(final File file, final int maxPageCount,
                                             final int intervalInSeconds) {
        final Intent intent = viewActionIntentWithMimeType(file);
        intent.setComponent(getKreaderComponentName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(TAG_AUTO_SLIDE_SHOW_MODE, true);
        intent.putExtra(TAG_SLIDE_SHOW_MAX_PAGE_COUNT, maxPageCount);
        intent.putExtra(TAG_SLIDE_SHOW_INTERVAL_IN_SECONDS, intervalInSeconds);

        return intent;
    }

    public static Intent viewActionIntentWithMimeType(final File file) {
        final Intent intent = viewActionIntent(file);
        final String extensionName = FileUtils.getFileExtension(file);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extensionName);
        if (StringUtils.isNullOrEmpty(mimeType)) {
            mimeType = MimeTypeUtils.mimeType(extensionName);
        }

        if (!StringUtils.isNullOrEmpty(mimeType)) {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        } else {
            intent.setData(Uri.fromFile(new File("dummy." + extensionName)));
        }
        return intent;
    }

    public static Intent enableReaderDebugLogIntent() {
        final Intent intent = new Intent(ACTION_ENABLE_READER_DEBUG_LOG);
        return intent;
    }

    public static Intent disableReaderDebugLogIntent() {
        final Intent intent = new Intent(ACTION_DISABLE_READER_DEBUG_LOG);
        return intent;
    }

    private static ComponentName getKreaderComponentName() {
        String packageName = "com.onyx.kreader";
        String className = packageName + ".ui.ReaderTabHostActivity";
        return new ComponentName(packageName, className);
    }

    public static ComponentName getReaderComponentName(Context context) {
        ActivityInfo activityInfo = null;
        ComponentName componentName = getKreaderComponentName();
        try {
            activityInfo = context.getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (activityInfo == null) {
            return null;
        }
        return componentName;
    }
}
