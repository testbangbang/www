package com.onyx.android.sdk.data.provider;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.List;

/**
 * Created by zhuzeng on 8/27/16.
 */
public interface DataProviderBase {

    void clearMetadata();

    void saveMetadata(final Context context, final Metadata metadata);

    Metadata findMetadata(final Context context, final String path, String md5);

    Metadata loadMetadata(final Context context, final String path, String md5);

    List<Metadata> findMetadata(final Context context, final QueryArgs queryArgs);

    long count(final Context context, final QueryArgs queryArgs);

    void removeMetadata(final Context context, final Metadata metadata);

    boolean saveDocumentOptions(final Context context, final String path, String md5, final String json);

    List<Annotation> loadAnnotations(final String application, final String md5, final int pageNumber, final OrderBy orderBy);

    List<Annotation> loadAnnotations(final String application, final String md5, final OrderBy orderBy);

    void addAnnotation(final Annotation annotation);

    void updateAnnotation(final Annotation annotation);

    void deleteAnnotation(final Annotation annotation);


    Bookmark loadBookmark(final String application, final String md5, final int pageNumber);

    List<Bookmark> loadBookmarks(final String application, final String md5, final OrderBy orderBy);

    void addBookmark(final Bookmark bookmark);

    void deleteBookmark(final Bookmark bookmark);

    Library loadLibrary(String uniqueId);

    List<Library> loadAllLibrary(String parentId);

    void addLibrary(Library library);

    void updateLibrary(Library library);

    void deleteLibrary(Library library);

    void clearLibrary();

    void clearThumbnail();

    List<Thumbnail> addThumbnail(Context context, String sourceMD5, Bitmap saveBitmap);

    void updateThumbnail(Thumbnail thumbnail);

    void deleteThumbnail(Thumbnail thumbnail);

    List<Thumbnail> loadThumbnail(Context context, String sourceMd5);

    Thumbnail loadThumbnail(Context context, String sourceMd5, ThumbnailKind kind);

    Bitmap loadThumbnailBitmap(Context context, String sourceMd5, ThumbnailKind kind);

    Bitmap loadThumbnailBitmap(Context context, Thumbnail thumbnail);

    void clearMetadataCollection();

    void addMetadataCollection(Context context, MetadataCollection collection);

    void deleteMetadataCollection(Context context, String libraryUniqueId, String metadataMD5);

    void updateMetadataCollection(MetadataCollection collection);

    MetadataCollection loadMetadataCollection(Context context, String libraryUniqueId, String metadataMD5);

    List<MetadataCollection> loadMetadataCollection(Context context, String libraryUniqueId);
}
