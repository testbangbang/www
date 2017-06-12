package com.onyx.android.sdk.data.provider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.db.table.OnyxAnnotationProvider;
import com.onyx.android.sdk.data.db.table.OnyxBookmarkProvider;
import com.onyx.android.sdk.data.db.table.OnyxLibraryProvider;
import com.onyx.android.sdk.data.db.table.OnyxMetadataCollectionProvider;
import com.onyx.android.sdk.data.db.table.OnyxMetadataProvider;
import com.onyx.android.sdk.data.db.table.OnyxThumbnailProvider;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Annotation_Table;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.data.model.Bookmark_Table;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Library_Table;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.MetadataCollection_Table;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.model.Thumbnail_Table;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.io.File;
import java.util.List;

/**
 * Created by suicheng on 2017/4/13.
 */

public class RemoteDataProvider implements DataProviderBase {
    private static final String TAG = RemoteDataProvider.class.getSimpleName();

    @Override
    public void clearMetadata() {
        FlowManager.getContext().getContentResolver().delete(OnyxMetadataProvider.CONTENT_URI,
                null, null);
    }

    public Metadata findMetadataByIdString(final Context context, final String idString) {
        Metadata metadata = null;
        try {
            metadata = ContentUtils.querySingle(OnyxMetadataProvider.CONTENT_URI,
                    Metadata.class, ConditionGroup.clause().and(Metadata_Table.idString.eq(idString)), null);
        } catch (Exception e) {
        } finally {
            return MetadataUtils.ensureObject(metadata);
        }
    }

    public Metadata findMetadataByPath(final Context context, final String path) {
        Metadata metadata = null;
        try {
            metadata = ContentUtils.querySingle(OnyxMetadataProvider.CONTENT_URI,
                    Metadata.class, ConditionGroup.clause().and(Metadata_Table.nativeAbsolutePath.eq(path)), null);
        } catch (Exception e) {
        } finally {
            return MetadataUtils.ensureObject(metadata);
        }
    }

    @Override
    public void saveMetadata(Context context, Metadata metadata) {
        metadata.beforeSave();
        Metadata findMeta = findMetadataByHashTag(context, metadata.getNativeAbsolutePath(), metadata.getHashTag());
        if (!findMeta.hasValidId()) {
            ContentUtils.insert(OnyxMetadataProvider.CONTENT_URI, metadata);
        } else {
            ContentUtils.update(OnyxMetadataProvider.CONTENT_URI, metadata);
        }
    }

    @Override
    public QueryResult<Metadata> findMetadataResultByQueryArgs(Context context, QueryArgs queryArgs) {
        QueryResult<Metadata> result = new QueryResult<>();
        result.list = findMetadataByQueryArgs(context, queryArgs);
        result.count = count(context, queryArgs);
        return result;
    }

    @Override
    public List<Metadata> findMetadataByQueryArgs(Context context, QueryArgs queryArgs) {
        return ContentUtils.queryList(OnyxMetadataProvider.CONTENT_URI,
                Metadata.class,
                queryArgs.conditionGroup,
                queryArgs.getOrderByQueryWithLimitOffset(),
                queryArgs.getProjectionSet());
    }

    @Override
    public Metadata findMetadataByHashTag(Context context, String path, String hashTag) {
        Metadata metadata = null;
        try {
            if (StringUtils.isNullOrEmpty(hashTag)) {
                hashTag = FileUtils.computeMD5(new File(path));
            }
            metadata = ContentUtils.querySingle(OnyxMetadataProvider.CONTENT_URI,
                    Metadata.class, ConditionGroup.clause().and(Metadata_Table.hashTag.eq(hashTag)), null);
        } catch (Exception e) {
        } finally {
            return MetadataUtils.ensureObject(metadata);
        }
    }

    @Override
    public long count(Context context, QueryArgs queryArgs) {
        Cursor cursor = null;
        try {
            cursor = FlowManager.getContext().getContentResolver().query(OnyxMetadataProvider.CONTENT_URI,
                    queryArgs.getProjectionSet(), queryArgs.conditionGroup.getQuery(), null, null);
            if (cursor == null) {
                Log.w(TAG, "queryArgs count cursor null");
                return 0;
            }
            return cursor.getCount();
        } finally {
            FileUtils.closeQuietly(cursor);
        }
    }

    @Override
    public void removeMetadata(Context context, Metadata metadata) {
        ContentUtils.delete(OnyxMetadataProvider.CONTENT_URI, metadata);
    }

    @Override
    public boolean saveDocumentOptions(Context context, String path, String associationId, String json) {
        try {
            Metadata document = findMetadataByHashTag(context, path, associationId);
            document.setExtraAttributes(json);
            document.beforeSave();
            if (!document.hasValidId()) {
                document.setHashTag(associationId);
                ContentUtils.insert(OnyxMetadataProvider.CONTENT_URI, document);
            } else {
                ContentUtils.update(OnyxMetadataProvider.CONTENT_URI, document);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Annotation> loadAnnotations(String application, String associationId, int pageNumber, OrderBy orderBy) {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .and(Annotation_Table.idString.eq(associationId))
                .and(Annotation_Table.application.eq(application))
                .and(Annotation_Table.pageNumber.eq(pageNumber));
        return ContentUtils.queryList(OnyxAnnotationProvider.CONTENT_URI,
                Annotation.class,
                conditionGroup,
                orderBy.getQuery());
    }

    @Override
    public List<Annotation> loadAnnotations(String application, String associationId, OrderBy orderBy) {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .and(Annotation_Table.idString.eq(associationId))
                .and(Annotation_Table.application.eq(application));
        return ContentUtils.queryList(OnyxAnnotationProvider.CONTENT_URI,
                Annotation.class,
                conditionGroup,
                orderBy.getQuery());
    }

    @Override
    public void addAnnotation(Annotation annotation) {
        annotation.beforeSave();
        ContentUtils.insert(OnyxAnnotationProvider.CONTENT_URI, annotation);
    }

    @Override
    public void updateAnnotation(Annotation annotation) {
        annotation.beforeSave();
        ContentUtils.update(OnyxAnnotationProvider.CONTENT_URI, annotation);
    }

    @Override
    public void deleteAnnotation(Annotation annotation) {
        ContentUtils.delete(OnyxAnnotationProvider.CONTENT_URI, annotation);
    }

    @Override
    public Bookmark loadBookmark(String application, String associationId, int pageNumber) {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .and(Bookmark_Table.idString.eq(associationId))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.pageNumber.eq(pageNumber));
        return ContentUtils.querySingle(OnyxBookmarkProvider.CONTENT_URI,
                Bookmark.class,
                conditionGroup,
                null);
    }

    @Override
    public List<Bookmark> loadBookmarks(String application, String associationId, OrderBy orderBy) {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .and(Bookmark_Table.idString.eq(associationId))
                .and(Bookmark_Table.application.eq(application));
        return ContentUtils.queryList(OnyxBookmarkProvider.CONTENT_URI,
                Bookmark.class,
                conditionGroup,
                orderBy.getQuery());
    }

    @Override
    public void addBookmark(Bookmark bookmark) {
        bookmark.beforeSave();
        ContentUtils.insert(OnyxBookmarkProvider.CONTENT_URI, bookmark);
    }

    @Override
    public void deleteBookmark(Bookmark bookmark) {
        ContentUtils.delete(OnyxBookmarkProvider.CONTENT_URI, bookmark);
    }

    private Condition getNullOrEqualCondition(Property<String> property, String compare) {
        return compare == null ? property.isNull() : property.eq(compare);
    }

    private Condition getNotNullOrEqualCondition(Property<String> property, String compare) {
        return compare == null ? property.isNotNull() : property.eq(compare);
    }

    @Override
    public Library loadLibrary(String uniqueId) {
        return ContentUtils.querySingle(OnyxLibraryProvider.CONTENT_URI,
                Library.class,
                ConditionGroup.clause().and(Library_Table.idString.eq(uniqueId)),
                null);
    }

    @Override
    public List<Library> loadAllLibrary(String parentId, QueryArgs queryArgs) {
        Condition condition = getNullOrEqualCondition(Library_Table.parentUniqueId, parentId);
        return ContentUtils.queryList(OnyxLibraryProvider.CONTENT_URI,
                Library.class,
                ConditionGroup.clause().and(condition),
                null);
    }

    @Override
    public void addLibrary(Library library) {
        library.beforeSave();
        ContentUtils.insert(OnyxLibraryProvider.CONTENT_URI, library);
    }

    @Override
    public void updateLibrary(Library library) {
        library.beforeSave();
        ContentUtils.update(OnyxLibraryProvider.CONTENT_URI, library);
    }

    @Override
    public void deleteLibrary(Library library) {
        ContentUtils.delete(OnyxLibraryProvider.CONTENT_URI, library);
    }

    @Override
    public void clearLibrary() {
        FlowManager.getContext().getContentResolver().delete(OnyxLibraryProvider.CONTENT_URI,
                null, null);
    }

    @Override
    public void clearThumbnails() {
        FlowManager.getContext().getContentResolver().delete(OnyxThumbnailProvider.CONTENT_URI,
                null, null);
    }

    @Override
    public void saveThumbnailEntry(Context context, Thumbnail thumbnail) {
        thumbnail.beforeSave();
        Thumbnail findThumbnail = getThumbnailEntry(context, thumbnail.getIdString(), thumbnail.getThumbnailKind());
        if (findThumbnail == null || !findThumbnail.hasValidId()) {
            ContentUtils.insert(OnyxThumbnailProvider.CONTENT_URI, thumbnail);
        } else {
            ContentUtils.update(OnyxThumbnailProvider.CONTENT_URI, thumbnail);
        }
    }

    public Thumbnail getThumbnailEntry(Context context, String associationId, final OnyxThumbnail.ThumbnailKind kind) {
        ConditionGroup group = ConditionGroup.clause().and(Thumbnail_Table.idString.eq(associationId))
                .and(Thumbnail_Table.thumbnailKind.eq(kind));
        return ContentUtils.querySingle(context.getContentResolver(),
                OnyxThumbnailProvider.CONTENT_URI,
                Thumbnail.class,
                group,
                null);
    }

    public void deleteThumbnailEntry(Thumbnail thumbnail) {
        ContentUtils.delete(OnyxThumbnailProvider.CONTENT_URI, thumbnail);
    }

    @Override
    public boolean saveThumbnailBitmap(Context context, String associationId, final OnyxThumbnail.ThumbnailKind kind, Bitmap saveBitmap) {
        return false;
    }

    public boolean removeThumbnailBitmap(Context context, String associationId, OnyxThumbnail.ThumbnailKind kind) {
        ConditionGroup group = ConditionGroup.clause().and(Thumbnail_Table.idString.eq(associationId))
                .and(Thumbnail_Table.thumbnailKind.eq(kind));
        int row = FlowManager.getContext().getContentResolver().delete(OnyxThumbnailProvider.CONTENT_URI,
                group.getQuery(),
                null);
        return row != 0;
    }

    public Bitmap getThumbnailBitmap(Context context, String associationId, final OnyxThumbnail.ThumbnailKind kind) {
        Thumbnail thumbnail = getThumbnailEntry(context, associationId, kind);
        if (thumbnail == null || StringUtils.isNullOrEmpty(thumbnail.getImageDataPath())) {
            return null;
        }
        return BitmapUtils.loadBitmapFromFile(thumbnail.getImageDataPath());
    }

    public List<Thumbnail> loadThumbnail(Context context, String associationId) {
        ConditionGroup group = ConditionGroup.clause().and(Thumbnail_Table.idString.eq(associationId));
        return ContentUtils.queryList(context.getContentResolver(),
                OnyxThumbnailProvider.CONTENT_URI,
                Thumbnail.class,
                group,
                null);
    }

    @Override
    public void clearMetadataCollection() {
        FlowManager.getContext().getContentResolver().delete(OnyxMetadataCollectionProvider.CONTENT_URI,
                null, null);
    }

    @Override
    public void addMetadataCollection(Context context, MetadataCollection collection) {
        collection.beforeSave();
        ContentUtils.insert(OnyxMetadataCollectionProvider.CONTENT_URI, collection);
    }

    @Override
    public void deleteMetadataCollection(Context context, String libraryUniqueId, String associationId) {
        ConditionGroup group = ConditionGroup.clause().and(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId));
        if (StringUtils.isNotBlank(associationId)) {
            group.and(MetadataCollection_Table.documentUniqueId.eq(associationId));
        }
        FlowManager.getContext().getContentResolver().delete(OnyxMetadataCollectionProvider.CONTENT_URI,
                group.getQuery(),
                null);
    }

    @Override
    public void deleteMetadataCollection(Context context, String libraryUniqueId) {
        FlowManager.getContext().getContentResolver().delete(OnyxMetadataCollectionProvider.CONTENT_URI,
                ConditionGroup.clause().and(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId)).getQuery(),
                null);
    }

    @Override
    public void deleteMetadataCollectionByDocId(Context context, String docId) {
        FlowManager.getContext().getContentResolver().delete(OnyxMetadataCollectionProvider.CONTENT_URI,
                ConditionGroup.clause().and(MetadataCollection_Table.documentUniqueId.eq(docId)).getQuery(),
                null);
    }

    @Override
    public void updateMetadataCollection(MetadataCollection collection) {
        collection.beforeSave();
        ContentUtils.update(OnyxMetadataCollectionProvider.CONTENT_URI, collection);
    }

    @Override
    public MetadataCollection loadMetadataCollection(Context context, String libraryUniqueId, String associationId) {
        ConditionGroup group = ConditionGroup.clause()
                .and(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .and(MetadataCollection_Table.documentUniqueId.eq(associationId));
        return ContentUtils.querySingle(OnyxMetadataCollectionProvider.CONTENT_URI,
                MetadataCollection.class,
                group,
                null);
    }

    @Override
    public List<MetadataCollection> loadMetadataCollection(Context context, String libraryUniqueId) {
        ConditionGroup group = ConditionGroup.clause()
                .and(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId));
        return ContentUtils.queryList(OnyxMetadataCollectionProvider.CONTENT_URI,
                MetadataCollection.class,
                group,
                null);
    }

    @Override
    public MetadataCollection findMetadataCollection(Context context, String associationId) {
        ConditionGroup group = ConditionGroup.clause()
                .and(MetadataCollection_Table.documentUniqueId.eq(associationId));
        return ContentUtils.querySingle(OnyxMetadataCollectionProvider.CONTENT_URI,
                MetadataCollection.class,
                group,
                null);
    }
}
