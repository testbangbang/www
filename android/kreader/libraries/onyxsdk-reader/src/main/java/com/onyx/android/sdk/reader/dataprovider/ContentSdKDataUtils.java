package com.onyx.android.sdk.reader.dataprovider;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by suicheng on 2017/4/12.
 */

public class ContentSdKDataUtils {
    private static final String TAG = ContentSdKDataUtils.class.getSimpleName();

    public static boolean saveMetadata(final Context context, final String documentPath,
                                       final ReaderDocumentMetadata docMetadata) {
        Metadata metadata = getMetadataByPath(context, documentPath);
        if (metadata == null || !metadata.hasValidId()) {
            metadata = Metadata.createFromFile(new File(documentPath), true);
        }
        if (metadata == null) {
            return false;
        }
        if (StringUtils.isNullOrEmpty(metadata.getHashTag())) {
            try {
                String md5 = FileUtils.computeMD5(new File(documentPath));
                metadata.setHashTag(md5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateDataWithDocMetadata(metadata, docMetadata);
        getDataProvider().saveMetadata(context, metadata);
        return true;
    }

    public static boolean updateProgress(final Context context, final String documentPath,
                                         final int currentPage, final int totalPage) {
        Metadata data = getMetadataByPath(context, documentPath);
        if (data == null) {
            data = Metadata.createFromFile(new File(documentPath), true);
        }
        if (data == null) {
            Log.w(TAG, "updateProgress: create file metadata failed, " + documentPath);
            return false;
        }
        data.setProgress(currentPage + 1, totalPage);
        boolean finished = (currentPage + 1 == totalPage);
        data.setReadingStatus(finished ? Metadata.ReadingStatus.FINISHED : Metadata.ReadingStatus.READING);
        data.updateLastAccess();
        getDataProvider().saveMetadata(context, data);
        return true;
    }

    public static boolean saveThumbnail(final Context context, final String documentPath,
                                        final Bitmap bitmap) {
        Metadata data = getMetadataByPath(context, documentPath);
        if (data == null) {
            Log.w(TAG, "saveThumbnailEntry: create file metadata failed, " + documentPath);
            return false;
        }
        if (hasThumbnail(context, documentPath)) {
            return true;
        }
        return ThumbnailUtils.insertThumbnail(context, getDataProvider(), data.getNativeAbsolutePath(),
                data.getHashTag(), bitmap);
    }

    public static boolean hasThumbnail(final Context context, final String documentPath) {
        boolean hasBitmap = false;
        Metadata metadata = getMetadataByPath(context, documentPath);
        if (metadata != null && StringUtils.isNotBlank(metadata.getHashTag())) {
            Thumbnail thumbnail = getDataProvider().getThumbnailEntry(context,
                    metadata.getAssociationId(), OnyxThumbnail.ThumbnailKind.Original);
            hasBitmap = thumbnail != null;
        }
        return hasBitmap;
    }

    private static Metadata getMetadataByPath(Context context, final String documentPath) {
        return getDataProvider().findMetadataByHashTag(context, documentPath, null);
    }

    private static void updateDataWithDocMetadata(final Metadata metadata, final ReaderDocumentMetadata docMetadata) {
        metadata.setTitle(docMetadata.getTitle());
        metadata.setDescription(docMetadata.getDescription());
        List<String> authors = docMetadata.getAuthors();
        if (!CollectionUtils.isNullOrEmpty(authors)) {
            metadata.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        }
        metadata.setPublisher(docMetadata.getPublisher());
    }

    public static DataProviderBase getDataProvider() {
        return DataProviderManager.getRemoteDataProvider();
    }
}
