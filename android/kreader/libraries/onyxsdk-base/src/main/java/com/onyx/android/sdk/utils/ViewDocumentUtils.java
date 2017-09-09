package com.onyx.android.sdk.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.List;

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

    public static Intent viewActionIntent(final File file, int... flags) {
        final Intent intent = viewActionIntent(file);
        if (flags != null && flags.length > 0) {
            for (int flag : flags) {
                intent.addFlags(flag);
            }
        }
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

    // may use in queryIntentActivities
    static public Intent mimeTypeIntent(final File file) {
        final Intent intent = viewActionIntent(file);
        String mimeType = getFileMimeType(file);
        if (!StringUtils.isNullOrEmpty(mimeType)) {
            intent.setDataAndType(Uri.fromFile(file), mimeType);
        } else {
            intent.setData(Uri.fromFile(new File("dummy." + FileUtils.getFileExtension(file))));
        }
        return intent;
    }

    public static String getFileMimeType(File file) {
        final String extensionName = FileUtils.getFileExtension(file);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extensionName);
        if (StringUtils.isNullOrEmpty(mimeType)) {
            mimeType = MimeTypeUtils.mimeType(extensionName);
        }
        return mimeType;
    }

    public static Intent viewActionIntentWithMimeType(final File file, int... flags) {
        Intent intent = viewActionIntent(file, flags);
        String mimeType = getFileMimeType(file);
        if (StringUtils.isNotBlank(mimeType)) {
            intent.setDataAndType(intent.getData(), mimeType);
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
        return getReaderComponentName(context, getKreaderComponentName());
    }

    public static ComponentName getEduReaderComponentName() {
        String packageName = "com.onyx.edu.reader";
        String className = packageName + ".ui.ReaderTabHostActivity";
        return new ComponentName(packageName, className);
    }

    public static ComponentName getEduReaderComponentName(Context context) {
        return getReaderComponentName(context, getEduReaderComponentName());
    }

    public static ComponentName getReaderComponentName(Context context, ComponentName componentName) {
        ActivityInfo activityInfo = null;
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

    static public ResolveInfo getDefaultActivityInfo(Context context, final File file, String defaultPackageName) {
        Intent intent = viewActionIntent(file);
        return getDefaultActivityInfo(context, intent, defaultPackageName);
    }

    static public ResolveInfo getDefaultActivityInfo(Context context, final Intent intent, String defaultPackageName) {
        List<ResolveInfo> infoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (CollectionUtils.isNullOrEmpty(infoList)) {
            return null;
        }
        if (StringUtils.isNullOrEmpty(defaultPackageName)) {
            return infoList.get(0);
        }
        for (ResolveInfo resolveInfo : infoList) {
            if (resolveInfo.activityInfo.packageName.equals(defaultPackageName)) {
                return resolveInfo;
            }
        }
        return infoList.get(0);
    }
}
