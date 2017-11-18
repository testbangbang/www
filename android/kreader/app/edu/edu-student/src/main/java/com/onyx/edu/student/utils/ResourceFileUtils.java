package com.onyx.edu.student.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.AppPreference;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

/**
 * Created by suicheng on 2017/10/24.
 */

public class ResourceFileUtils {

    public static boolean checkBookMetadataPathValid(Metadata book) {
        if (StringUtils.isNotBlank(book.getNativeAbsolutePath()) && new File(book.getNativeAbsolutePath()).exists()) {
            return true;
        }
        return false;
    }

    public static String getDataSaveFilePath(Context context, Metadata book) {
        if (checkBookMetadataPathValid(book)) {
            return book.getNativeAbsolutePath();
        }
        String fileName = FileUtils.fixNotAllowFileName(book.getName() + "." + book.getType());
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        return new File(CloudUtils.dataCacheDirectory(context.getApplicationContext(), book.getGuid()), fileName)
                .getAbsolutePath();
    }

    public static boolean isFileExists(Context context, Metadata book) {
        if (book == null || StringUtils.isNullOrEmpty(book.getGuid())) {
            return false;
        }
        File dir = CloudUtils.dataCacheDirectory(context.getApplicationContext(), book.getGuid());
        if (dir.list() == null || dir.list().length <= 0) {
            return false;
        }
        String path = getDataSaveFilePath(context, book);
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists() && file.length() <= 0) {
            return false;
        }
        return true;
    }

    public static void openFile(Context context, Metadata book) {
        String path = getDataSaveFilePath(context, book);
        if (StringUtils.isNullOrEmpty(path)) {
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        Intent intent = MetadataUtils.putIntentExtraDataMetadata(
                ViewDocumentUtils.viewActionIntentWithMimeType(file), book);
        AppPreference app = AppPreference.getFileAppPreferMap().get(FilenameUtils.getExtension(file.getName()));
        ComponentName componentName;
        if (app != null) {
            componentName = new ComponentName(app.packageName, app.className);
        } else {
            componentName = ViewDocumentUtils.getEduReaderComponentName();
        }
        ResolveInfo info = ViewDocumentUtils.getDefaultActivityInfo(context, intent, componentName.getPackageName());
        if (info == null) {
            return;
        }
        ActivityUtil.startActivitySafely(context, intent, info.activityInfo);
    }

    public static boolean openFileIfExist(Context context, Metadata book) {
        if (ResourceFileUtils.isFileExists(context, book)) {
            ResourceFileUtils.openFile(context, book);
            return true;
        }
        return false;
    }
}
