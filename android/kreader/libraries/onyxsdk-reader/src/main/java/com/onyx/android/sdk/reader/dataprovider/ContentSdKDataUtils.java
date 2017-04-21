package com.onyx.android.sdk.reader.dataprovider;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.db.table.OnyxMetadataProvider;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

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
        if (!metadata.hasValidId()) {
            ContentUtils.insert(context.getContentResolver(), OnyxMetadataProvider.CONTENT_URI, metadata);
        } else {
            ContentUtils.update(context.getContentResolver(), OnyxMetadataProvider.CONTENT_URI, metadata);
        }
        return true;
    }

    public static boolean updateProgress(final Context context, final String documentPath,
                                         final int currentPage, final int totalPage) {
        Metadata data = getMetadataByPath(context, documentPath);
        if (data == null) {
            data = Metadata.createFromFile(new File(documentPath));
        }
        if (data == null) {
            Log.w(TAG, "updateProgress: create file metadata failed, " + documentPath);
            return false;
        }
        data.setProgress(currentPage + 1, totalPage);
        boolean finished = (currentPage + 1 == totalPage);
        data.setReadingStatus(finished ? Metadata.ReadingStatus.FINISHED : Metadata.ReadingStatus.READING);
        data.updateLastAccess();
        return ContentUtils.update(context.getContentResolver(), OnyxMetadataProvider.CONTENT_URI,
                data) != 0;
    }

    public static boolean saveThumbnail(final Context context, final String documentPath,
                                        final Bitmap bitmap) {
        Metadata data = getMetadataByPath(context, documentPath);
        if (data == null) {
            Log.w(TAG, "saveThumbnail: create file metadata failed, " + documentPath);
            return false;
        }
        if (hasThumbnail(context, documentPath)) {
            return true;
        }
        return ThumbnailUtils.insertThumbnail(context, DataProviderManager.getRemoteDataProvider(),
                data.getNativeAbsolutePath(), data.getHashTag(), bitmap);
    }

    public static boolean hasThumbnail(final Context context, final String documentPath) {
        boolean hasBitmap = false;
        Metadata metadata = getMetadataByPath(context, documentPath);
        if (metadata != null && StringUtils.isNotBlank(metadata.getHashTag())) {
            Thumbnail thumbnail = DataProviderManager.getRemoteDataProvider().getThumbnail(context,
                    metadata.getHashTag(), OnyxThumbnail.ThumbnailKind.Original);
            hasBitmap = thumbnail != null;
        }
        return hasBitmap;
    }

    private static Metadata getMetadataByPath(Context context, final String documentPath) {
        return DataProviderManager.getRemoteDataProvider().findMetadataByPath(context, documentPath);
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
}
