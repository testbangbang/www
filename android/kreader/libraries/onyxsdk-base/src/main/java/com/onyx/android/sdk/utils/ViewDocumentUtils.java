package com.onyx.android.sdk.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

/**
 * Created by suicheng on 2016/12/5.
 */
public class ViewDocumentUtils {

    public static Intent viewActionIntent(final File file) {
        final Intent intent = new Intent();
        intent.setData(Uri.fromFile(file));
        intent.setAction(Intent.ACTION_VIEW);
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

    private static ComponentName getKreaderComponentName() {
        String packageName = "com.onyx.kreader";
        String className = packageName + ".ui.ReaderActivity";
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
