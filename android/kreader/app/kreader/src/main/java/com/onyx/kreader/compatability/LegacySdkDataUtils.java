package com.onyx.kreader.compatability;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.onyx.kreader.api.ReaderDocumentMetadata;

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
        OnyxMetadata data = getMetadataByPath(documentPath);
        if (data == null) {
            Log.w(TAG, "saveMetadata: create file metadata failed, " + documentPath);
            return false;
        }
        initDataWithDocumentMetadata(data, metadata);
        return OnyxCmsCenter.insertMetadata(context, data);
    }

    public static boolean updateProgress(final Context context, final String documentPath,
                                         final int currentPage, final int totalPage) {
        OnyxMetadata data = getMetadataByPath(documentPath);
        if (data == null) {
            Log.w(TAG, "updateProgress: create file metadata failed, " + documentPath);
            return false;
        }
        OnyxCmsCenter.getMetadata(context, data);
        data.setProgress(new OnyxBookProgress(currentPage + 1, totalPage));
        data.updateLastAccess();
        return false;
    }

    public static boolean saveThumbnail(final Context context, final String documentPath,
                                        final Bitmap bitmap) {
        OnyxMetadata data = getMetadataByPath(documentPath);
        if (data == null) {
            Log.w(TAG, "saveThumbnail: create file metadata failed, " + documentPath);
            return false;
        }
        // insert thumbnail is heavy, so we check it first
        if (OnyxCmsCenter.hasThumbnail(context, data)) {
            return true;
        }
        return OnyxCmsCenter.insertThumbnail(context, data, bitmap);
    }

    private static OnyxMetadata getMetadataByPath(final String documentPath) {
        OnyxMetadata data = metadataCache.get(documentPath);
        if (data == null) {
            data = OnyxMetadata.createFromFile(documentPath);
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
