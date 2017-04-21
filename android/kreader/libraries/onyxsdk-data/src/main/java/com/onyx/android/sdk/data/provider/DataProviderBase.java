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
import com.onyx.android.sdk.utils.BitmapUtils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.List;

/**
 * Created by zhuzeng on 8/27/16.
 */
public interface DataProviderBase {

    void clearMetadata();

    void saveMetadata(final Context context, final Metadata metadata);

    List<Metadata> findMetadataByQueryArgs(final Context context, final QueryArgs queryArgs);

    Metadata findMetadataByPath(final Context context, final String path);

    Metadata findMetadataByHashTag(final Context context, final String path, String hashTag);

    long count(final Context context, final QueryArgs queryArgs);

    void removeMetadata(final Context context, final Metadata metadata);

    boolean saveDocumentOptions(final Context context, final String path, String associationId, final String json);

    List<Annotation> loadAnnotations(final String application, final String associationId, final int pageNumber, final OrderBy orderBy);

    List<Annotation> loadAnnotations(final String application, final String associationId, final OrderBy orderBy);

    void addAnnotation(final Annotation annotation);

    void updateAnnotation(final Annotation annotation);

    void deleteAnnotation(final Annotation annotation);


    Bookmark loadBookmark(final String application, final String associationId, final int pageNumber);

    List<Bookmark> loadBookmarks(final String application, final String associationId, final OrderBy orderBy);

    void addBookmark(final Bookmark bookmark);

    void deleteBookmark(final Bookmark bookmark);

    Library loadLibrary(String uniqueId);

    List<Library> loadAllLibrary(String parentId);

    void addLibrary(Library library);

    void updateLibrary(Library library);

    void deleteLibrary(Library library);

    void clearLibrary();

    void clearAllThumbnails();

    void saveThumbnail(Context context, Thumbnail thumbnail);

    boolean setThumbnail(Context context, String associationId, final Bitmap saveBitmap, ThumbnailKind kind);

    boolean removeThumbnail(Context context, String associationId, ThumbnailKind kind);

    Thumbnail getThumbnail(Context context, String associationId, final ThumbnailKind kind);

    Bitmap getThumbnailBitmap(Context context, String associationId, final ThumbnailKind kind);

    void clearMetadataCollection();

    void addMetadataCollection(Context context, MetadataCollection collection);

    void deleteMetadataCollection(Context context, String libraryUniqueId, String associationId);

    void deleteMetadataCollection(Context context, String libraryUniqueId);

    void deleteMetadataCollectionByDocId(Context context, String docId);

    void updateMetadataCollection(MetadataCollection collection);

    MetadataCollection loadMetadataCollection(Context context, String libraryUniqueId, String associationId);

    List<MetadataCollection> loadMetadataCollection(Context context, String libraryUniqueId);

    MetadataCollection findMetadataCollection(Context context, String associationId);
}
