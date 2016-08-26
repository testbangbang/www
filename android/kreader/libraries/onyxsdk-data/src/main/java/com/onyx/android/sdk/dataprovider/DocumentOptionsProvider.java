package com.onyx.android.sdk.dataprovider;

import android.content.Context;
import android.util.Log;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;

/**
 * Created by zhuzeng on 5/27/16.
 * serves as proxy between request and function provider. it may forward request to real impl provider like
 * onyx android sdk or new sdk.
 */
public class DocumentOptionsProvider {

    public static final String TAG = DocumentOptionsProvider.class.getSimpleName();

    public static DocumentOptions findDocumentOptions(final Context context, final String path, String md5) {
        DocumentOptions options = null;
        try {
            if (StringUtils.isNullOrEmpty(md5)) {
                md5 = FileUtils.computeMD5(new File(path));
            }
            options = new Select().from(DocumentOptions.class).where(DocumentOptions_Table.md5.eq(md5)).querySingle();
            return options;
        } catch (Exception e) {
        }
        return options;
    }

    public static DocumentOptions loadDocumentOptions(final Context context, final String path, String md5) {
        DocumentOptions options = findDocumentOptions(context, path, md5);
        if (options == null) {
            options = new DocumentOptions();
        }
        return options;
    }

    public static boolean saveDocumentOptions(final Context context, final String path, String md5, final String json) {
        try {
            DocumentOptions documentOptions;
            final DocumentOptions options = DocumentOptionsProvider.findDocumentOptions(context, path, md5);
            if (options == null) {
                documentOptions = new DocumentOptions();
                documentOptions.setMd5(md5);
            } else {
                documentOptions = options;
            }
            documentOptions.setExtraAttributes(json);
            if (options == null) {
                documentOptions.save();
            } else {
                documentOptions.update();
            }
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
            return false;
        }
    }

}
