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
public class DocumentProvider {

    public static final String TAG = DocumentProvider.class.getSimpleName();

    public static Document findDocument(final Context context, final String path, String md5) {
        Document document = null;
        try {
            if (StringUtils.isNullOrEmpty(md5)) {
                md5 = FileUtils.computeMD5(new File(path));
            }
            document = new Select().from(Document.class).where(Document_Table.uniqueId.eq(md5)).querySingle();
            return document;
        } catch (Exception e) {
        }
        return document;
    }

    public static Document loadDocument(final Context context, final String path, String md5) {
        Document document = findDocument(context, path, md5);
        if (document == null) {
            document = new Document();
        }
        return document;
    }

    public static boolean saveDocumentOptions(final Context context, final String path, String md5, final String json) {
        try {
            Document document;
            final Document options = DocumentProvider.findDocument(context, path, md5);
            if (options == null) {
                document = new Document();
                document.setUniqueId(md5);
            } else {
                document = options;
            }
            document.setExtraAttributes(json);
            if (options == null) {
                document.save();
            } else {
                document.update();
            }
            return true;
        } catch (Exception e) {
            Log.w(TAG, e);
            return false;
        }
    }

}
