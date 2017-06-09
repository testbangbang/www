/**
 *
 */
package com.onyx.android.sdk.data.compatability;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.onyx.android.sdk.data.RefValue;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author joy
 */
public class OnyxCmsCenter {
    private static final String TAG = "OnyxCMSCenter";

    public static final String PROVIDER_AUTHORITY = "com.onyx.android.sdk.OnyxCmsProvider";

    /**
     * reading data from DB to metadata, old data in metadata will be overwritten
     *
     * @param context
     * @param data
     * @return
     */
    public static boolean getMetadata(Context context, OnyxMetadata data) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, OnyxMetadata.Columns.MD5 + "= ?", new String[]{data.getMD5()}, null);
            if (c == null) {
                Log.w(TAG, "getMetadata, query database failed");
                return false;
            }
            if (c.moveToFirst()) {
                OnyxMetadata.Columns.readColumnData(c, data);
                return true;
            }

            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean insertMetadata(Context context, OnyxMetadata data) {
        deleteMetadata(context, data);
        Uri result = context.getContentResolver().insert(
                OnyxMetadata.CONTENT_URI,
                OnyxMetadata.Columns.createColumnData(data));
        if (result == null) {
            return false;
        }
        String id = result.getLastPathSegment();
        if (id == null) {
            return false;
        }
        data.setId(Long.parseLong(id));
        return true;
    }

    public static boolean deleteMetadata(Context context, OnyxMetadata data) {
        if (StringUtils.isNotBlank(data.getMD5())) {
            if (context.getContentResolver().delete(OnyxMetadata.CONTENT_URI,
                    OnyxMetadata.Columns.MD5 + "=?",
                    new String[]{data.getMD5()}) > 0) {
                return true;
            }
        }
        return (context.getContentResolver().delete(OnyxMetadata.CONTENT_URI,
                OnyxMetadata.Columns.NATIVE_ABSOLUTE_PATH + "=?",
                new String[]{data.getNativeAbsolutePath()}) > 0);
    }

    public static boolean updateMetadata(Context context, OnyxMetadata data) {
        Uri row = Uri.withAppendedPath(OnyxMetadata.CONTENT_URI,
                String.valueOf(data.getId()));
        int count = context.getContentResolver().update(row,
                OnyxMetadata.Columns.createColumnData(data), null, null);
        if (count <= 0) {
            return false;
        }

        assert (count == 1);
        return true;
    }

    public static boolean hasThumbnail(Context context, OnyxMetadata metadata) {
        if (metadata == null) {
            assert (false);
            return false;
        }

        Cursor c = null;
        try {
            c = getThumbnailCursor(context, metadata.getMD5(), OnyxThumbnail.ThumbnailKind.Original);
            if (c == null) {
                Log.w(TAG, "query original thumbnail failed");
                return false;
            }
            return c.moveToFirst();
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static boolean getThumbnail(Context context, OnyxMetadata metadata,
                                       OnyxThumbnail.ThumbnailKind thumbnailKind, RefValue<Bitmap> result) {
        if (metadata == null) {
            assert (false);
            return false;
        }
        return getThumbnailByMD5(context, metadata.getMD5(), thumbnailKind, result);
    }

    public static boolean getThumbnail(Context context, OnyxMetadata metadata, RefValue<Bitmap> result) {
        return getThumbnail(context, metadata, OnyxThumbnail.ThumbnailKind.Original, result);
    }

    public static boolean getThumbnailByMD5(Context context, final String md5, OnyxThumbnail.ThumbnailKind thumbnailKind, RefValue<Bitmap> result) {
        if (StringUtils.isNullOrEmpty(md5)) {
            return false;
        }

        Cursor c = null;
        try {
            c = getThumbnailCursor(context, md5, thumbnailKind);
            if (c == null) {
                Log.w(TAG, "query thumbnail failed: " + thumbnailKind.toString() + ", try to query original thumbnail");
                c = getThumbnailCursor(context, md5, OnyxThumbnail.ThumbnailKind.Original);
                if (c == null) {
                    Log.w(TAG, "query original thumbnail failed");
                    return false;
                }
            }

            if (c.moveToFirst()) {
                OnyxThumbnail data = OnyxThumbnail.Columns.readColumnData(c);
                Uri row = Uri.withAppendedPath(OnyxThumbnail.CONTENT_URI,
                        String.valueOf(data.getId()));
                InputStream is = null;
                try {
                    is = context.getContentResolver().openInputStream(row);
                    if (is == null) {
                        Log.w(TAG, "openInputStream failed");
                        return false;
                    }
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inPurgeable = true;
                    Bitmap b = BitmapFactory.decodeStream(is, null, o);

                    result.setValue(b);
                    return true;
                } catch (Throwable tr) {
                    Log.e(TAG, "exception", tr);
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            Log.w(TAG, e);
                        }
                    }
                }
            }

            return false;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    /**
     * @param context
     * @param data
     * @param thumbnail
     * @return
     */
    public static boolean insertThumbnail(Context context, OnyxMetadata data,
                                          Bitmap thumbnail) {
        if (data == null) {
            assert (false);
            return false;
        }

        if (!insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Original)) {
            return false;
        }

        insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Large);
        insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Middle);
        insertThumbnailHelper(context, thumbnail, data.getMD5(), OnyxThumbnail.ThumbnailKind.Small);

        return true;
    }

    private static Cursor getThumbnailCursor(Context context, String md5, OnyxThumbnail.ThumbnailKind kind) {
        return context.getContentResolver().query(
                OnyxThumbnail.CONTENT_URI,
                null,
                OnyxThumbnail.Columns.SOURCE_MD5 + "='" + md5 +
                        "' AND " + OnyxThumbnail.Columns.THUMBNAIL_KIND + "='" +
                        kind.toString() + "'",
                null, null);
    }

    private static boolean insertThumbnailHelper(Context context, Bitmap bmp,
                                                 String md5, OnyxThumbnail.ThumbnailKind thumbnailKind) {
        Bitmap thumbnail = bmp;
        OutputStream os = null;

        try {
            switch (thumbnailKind) {
                case Original:
                    break;
                case Large:
                    thumbnail = OnyxThumbnail.createLargeThumbnail(bmp);
                    break;
                case Middle:
                    thumbnail = OnyxThumbnail.createMiddleThumbnail(bmp);
                    break;
                case Small:
                    thumbnail = OnyxThumbnail.createSmallThumbnail(bmp);
                    break;
                default:
                    assert (false);
                    break;
            }

            Uri result = context.getContentResolver().insert(
                    OnyxThumbnail.CONTENT_URI,
                    OnyxThumbnail.Columns.createColumnData(md5, thumbnailKind));
            if (result == null) {
                Log.w(TAG, "insertThumbnail db insert failed");
                return false;
            }

            os = context.getContentResolver().openOutputStream(result);
            if (os == null) {
                Log.w(TAG, "openOutputStream failed");
                return false;
            }
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, os);
            return true;
        } catch (FileNotFoundException e) {
            Log.w(TAG, e);
            return false;
        } finally {
            if (thumbnail != null && thumbnail != bmp) {
                thumbnail.recycle();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.w(TAG, e);
                }
            }
        }
    }

    public static OnyxMetadata getMetadataByCloudReference(Context context, final String cloudReference) {
        Cursor c = null;
        OnyxMetadata data = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, OnyxMetadata.Columns.CLOUD_REFERENCE + "= ?", new String[]{cloudReference}, null);
            if (c == null) {
                return null;
            }
            if (c.moveToFirst()) {
                data = OnyxMetadata.Columns.readColumnData(c);
            }
            return data;
        } finally {
            FileUtils.closeQuietly(c);
        }
    }

    public static OnyxMetadata getMetadataByMD5(Context context, String md5) {
        Cursor c = null;
        OnyxMetadata data = null;
        try {
            c = context.getContentResolver().query(OnyxMetadata.CONTENT_URI,
                    null, OnyxMetadata.Columns.MD5 + "= ?", new String[]{md5}, null);
            if (c == null) {
                Log.w(TAG, "getMetadatas, query database failed");
                return null;
            }
            if (c.moveToFirst()) {
                data = OnyxMetadata.Columns.readColumnData(c);
            }
            return data;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

    public static List<OnyxHistoryEntry> getHistoryByApplication(Context context, String application) {
        Cursor c = null;
        List<OnyxHistoryEntry> historyEntries = new ArrayList<OnyxHistoryEntry>();
        try {
            c = context.getContentResolver().query(OnyxHistoryEntry.CONTENT_URI,
                    null, OnyxHistoryEntry.Columns.APPLICATION + "= ?" , new String[]{application}, null);
            if (c == null) {
                Log.w(TAG, "getHistoryByApplication, query database failed");
                return null;
            }
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                OnyxHistoryEntry history_entry = OnyxHistoryEntry.Columns.readColumnsData(c);
                historyEntries.add(history_entry);
            }
            return historyEntries;
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

}
