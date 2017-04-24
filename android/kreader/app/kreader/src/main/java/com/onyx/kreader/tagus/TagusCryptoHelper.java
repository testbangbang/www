package com.onyx.kreader.tagus;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.onyx.android.sdk.data.compatability.OnyxCmsCenter;
import com.onyx.android.sdk.data.compatability.OnyxMetadata;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by jim on 17-4-20.
 */
public class TagusCryptoHelper {

    private static final String TAG = "TagusCryptoHelper";

    private static TagusDocumentCrypto getDocumentCrypto(final Context context, final String md5) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(TagusConstants.CONTENT_URI, null,
                    TagusDocumentCrypto.Columns.MD5 + "='" + md5 + "'",
                    null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                Log.w(TAG, "query TagusDocumentCrypto failed");
                return null;
            }
            return TagusDocumentCrypto.Columns.readColumnData(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean handleZipCompressedBooks(final Context context, final String filePath, final BaseOptions options) {
        OnyxMetadata metadata = OnyxMetadata.createFromFile(filePath);
        OnyxCmsCenter.getMetadata(context, metadata);
        if (metadata == null) {
            Log.e(TAG, "creating metadata failed");
            return false;
        }

        if (metadata.getISBN() == null || metadata.getISBN().isEmpty()) {
            return false;
        }

        TagusDocumentCrypto crypto = getDocumentCrypto(context, metadata.getMD5());
        if (crypto != null) {
            if (crypto.getEncType().equals(TagusEncryptionType.ZIP_COMPRESSED.toString())) {
                String password = TagusZipPasswordUtil.getZipFilePassword(crypto, metadata.getISBN());
                if (StringUtils.isNotBlank(password)) {
                    options.setZipPassword(password);
                    return true;
                }
            }
        }
        return false;
    }

}
