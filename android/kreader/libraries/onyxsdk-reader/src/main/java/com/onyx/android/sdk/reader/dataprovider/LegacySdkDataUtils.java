package com.onyx.android.sdk.reader.dataprovider;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.onyx.android.sdk.data.OnyxDictionaryInfo;
import com.onyx.android.sdk.data.compatability.OnyxBookProgress;
import com.onyx.android.sdk.data.compatability.OnyxCmsCenter;
import com.onyx.android.sdk.data.compatability.OnyxMetadata;
import com.onyx.android.sdk.data.compatability.OnyxSysCenter;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joy on 7/22/16.
 */
public class LegacySdkDataUtils {

    private final static String TAG = LegacySdkDataUtils.class.getSimpleName();

    /**
     * avoid heavy OnyxMetadata creation from path
     */
    private static HashMap<String, OnyxMetadata> metadataCache = new HashMap<>();

    public static boolean saveMetadata(final Context context, final String documentPath,
                                         final ReaderDocumentMetadata metadata) {
        return saveMetadata(context, documentPath, null, metadata);
    }

    public static boolean saveMetadata(final Context context,
                                       final String documentPath,
                                       final String documentMd5,
                                       final ReaderDocumentMetadata metadata) {
        try {
            OnyxMetadata data = getMetadataByPath(documentPath, documentMd5);
            if (data == null) {
                Log.w(TAG, "saveMetadata: create file metadata failed, " + documentPath);
                return false;
            }
            // getById metadata from cms will overwrite existing data, so we update metadata after it
            if (!OnyxCmsCenter.getMetadata(context, data)) {
                initDataWithDocumentMetadata(data, metadata);
                return OnyxCmsCenter.insertMetadata(context, data);
            }

            initDataWithDocumentMetadata(data, metadata);
            return OnyxCmsCenter.updateMetadata(context, data);
        } catch (Throwable tr) {
            return false;
        }
    }

    public static boolean updateProgress(final Context context, final String documentPath,
                                         final int currentPage, final int totalPage) {
        try {
            OnyxMetadata data = getMetadataByPath(documentPath, null);
            if (data == null) {
                Log.w(TAG, "updateProgress: create file metadata failed, " + documentPath);
                return false;
            }
            if (!OnyxCmsCenter.getMetadata(context, data)) {
                return false;
            }
            data.setProgress(new OnyxBookProgress(currentPage + 1, totalPage));
            data.updateLastAccess();
            return OnyxCmsCenter.updateMetadata(context, data);
        } catch (Throwable tr) {
            return false;
        }
    }

    public static boolean hasThumbnail(final Context context, final String documentPath) {
        OnyxMetadata data = getMetadataByPath(documentPath, null);
        if (data == null) {
            Log.w(TAG, "hasThumbnail: create file metadata failed, " + documentPath);
            return false;
        }
        return OnyxCmsCenter.hasThumbnail(context, data);
    }

    public static boolean saveThumbnail(final Context context, final String documentPath,
                                        final Bitmap bitmap) {
        try {
            OnyxMetadata data = getMetadataByPath(documentPath, null);
            if (data == null) {
                Log.w(TAG, "saveThumbnailEntry: create file metadata failed, " + documentPath);
                return false;
            }
            // insert thumbnail is heavy, so we check it first
            if (OnyxCmsCenter.hasThumbnail(context, data)) {
                return true;
            }
            return OnyxCmsCenter.insertThumbnail(context, data, bitmap);
        } catch (Throwable tr) {
            return false;
        }
    }

    public static OnyxDictionaryInfo getDictionary(final Context context) {
        try {
            return OnyxSysCenter.getDictionary(context);
        } catch (Throwable tr) {
            return OnyxDictionaryInfo.getDefaultDictionary();
        }
    }

    public static int getScreenUpdateGCInterval(final Context context, int defaultIntervalCount) {
        try {
            return OnyxSysCenter.getScreenUpdateGCInterval(context, defaultIntervalCount);
        } catch (Throwable tr) {
            return 5;
        }
    }

    public static void setScreenUpdateGCInterval(final Context context, final int newValue) {
        try {
            OnyxSysCenter.setScreenUpdateGCInterval(context, newValue);
        } catch (Throwable tr) {

        }
    }

    private static OnyxMetadata getMetadataByPath(final String documentPath, final String documentMd5) {
        OnyxMetadata data = metadataCache.get(documentPath);
        if (data == null) {
            if (StringUtils.isNullOrEmpty(documentMd5)) {
                data = OnyxMetadata.createFromFile(documentPath);
            } else {
                data = OnyxMetadata.createFromFile(new File(documentPath), false);
                data.setMD5(documentMd5);
            }
        }
        if (data == null) {
            return null;
        }
        metadataCache.put(documentPath, data);
        return data;
    }


    private static void initDataWithDocumentMetadata(final OnyxMetadata metadata, final ReaderDocumentMetadata documentMetadata) {
        metadata.setTitle(documentMetadata.getTitle());
        metadata.setDescription(documentMetadata.getDescription());
        metadata.setAuthors(new ArrayList<>(documentMetadata.getAuthors()));
        metadata.setPublisher(documentMetadata.getPublisher());
    }
}
