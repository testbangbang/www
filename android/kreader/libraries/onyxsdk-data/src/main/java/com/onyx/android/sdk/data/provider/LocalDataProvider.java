package com.onyx.android.sdk.data.provider;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.sql.language.property.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 8/27/16.
 */
public class LocalDataProvider implements DataProviderBase {

    public void clearMetadata() {
        Delete.table(Metadata.class);
    }

    public Metadata findMetadataByPath(final Context context, final String path) {
        Metadata metadata = null;
        try {
            metadata = new Select().from(Metadata.class).where(Metadata_Table.nativeAbsolutePath.eq(path)).querySingle();
        } catch (Exception e) {
        } finally {
            return MetadataUtils.ensureObject(metadata);
        }
    }

    public Metadata findMetadataByHashTag(final Context context, final String path, String hashTag) {
        Metadata metadata = null;
        try {
            if (StringUtils.isNullOrEmpty(hashTag)) {
                hashTag = FileUtils.computeMD5(new File(path));
            }
            metadata = new Select().from(Metadata.class).where(Metadata_Table.hashTag.eq(hashTag)).querySingle();
        } catch (Exception e) {
        } finally {
            return MetadataUtils.ensureObject(metadata);
        }
    }

    public List<Metadata> findMetadataByQueryArgs(final Context context, final QueryArgs queryArgs) {
        if (queryArgs.conditionGroup != null) {
            Where<Metadata> where = new Select(queryArgs.propertyList.toArray(new IProperty[0])).from(Metadata.class)
                    .where(queryArgs.conditionGroup);
            for (OrderBy orderBy : queryArgs.orderByList) {
                where.orderBy(orderBy);
            }
            return where.offset(queryArgs.offset).limit(queryArgs.limit).queryList();
        }
        return new ArrayList<>();
    }

    public long count(final Context context, final QueryArgs queryArgs) {
        return new Select(Method.count()).from(Metadata.class).where(queryArgs.conditionGroup).count();
    }

    public void saveMetadata(final Context context, final Metadata metadata) {
        metadata.save();
    }

    public void removeMetadata(final Context context, final Metadata metadata) {
        metadata.delete();
    }

    public boolean saveDocumentOptions(final Context context, final String path, String md5, final String json) {
        try {
            final Metadata document = findMetadataByHashTag(context, path, md5);
            document.setExtraAttributes(json);
            if (!document.hasValidId()) {
                document.setHashTag(md5);
                document.save();
            } else {
                document.update();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public final List<Annotation> loadAnnotations(final String application, final String md5, final int pageNumber, final OrderBy orderBy) {
        return new Select().from(Annotation.class).where(Annotation_Table.idString.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .and(Annotation_Table.pageNumber.eq(pageNumber))
                .orderBy(orderBy)
                .queryList();
    }

    public final List<Annotation> loadAnnotations(final String application, final String md5, final OrderBy orderBy) {
        return new Select().from(Annotation.class).where(Annotation_Table.idString.eq(md5))
                .and(Annotation_Table.application.eq(application))
                .orderBy(orderBy)
                .queryList();
    }

    public void addAnnotation(final Annotation annotation) {
        annotation.save();
    }

    public void updateAnnotation(final Annotation annotation) {
        annotation.save();
    }

    public void deleteAnnotation(final Annotation annotation) {
        annotation.delete();
    }

    public final Bookmark loadBookmark(final String application, final String md5, final int pageNumber) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.idString.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.pageNumber.eq(pageNumber))
                .querySingle();
    }

    public final List<Bookmark> loadBookmarks(final String application, final String md5, final OrderBy orderBy) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.idString.eq(md5))
                .and(Bookmark_Table.application.eq(application))
                .orderBy(orderBy)
                .queryList();
    }

    public void addBookmark(final Bookmark bookmark) {
        bookmark.save();
    }

    public void deleteBookmark(final Bookmark bookmark) {
        bookmark.delete();
    }

    private Condition getNullOrEqualCondition(Property<String> property, String compare) {
        return compare == null ? property.isNull() : property.eq(compare);
    }

    private Condition getNotNullOrEqualCondition(Property<String> property, String compare) {
        return compare == null ? property.isNotNull() : property.eq(compare);
    }

    @Override
    public Library loadLibrary(String uniqueId) {
        return new Select().from(Library.class).where(Library_Table.idString.eq(uniqueId)).querySingle();
    }

    @Override
    public List<Library> loadAllLibrary(String parentId) {
        Condition condition = getNullOrEqualCondition(Library_Table.parentUniqueId, parentId);
        return new Select().from(Library.class).where(condition).queryList();
    }

    @Override
    public void addLibrary(Library library) {
        library.save();
    }

    @Override
    public void updateLibrary(Library library) {
        library.update();
    }

    @Override
    public void deleteLibrary(Library library) {
        library.delete();
    }

    @Override
    public void clearLibrary() {
        Delete.table(Library.class);
    }

    @Override
    public void clearAllThumbnails() {
        Delete.table(Thumbnail.class);
    }

    @Override
    public void saveThumbnail(Context context, Thumbnail thumbnail) {
        thumbnail.save();
    }

    @Override
    public boolean setThumbnail(Context context, String associationId, final Bitmap saveBitmap, ThumbnailKind kind) {
        return false;
    }

    public boolean removeThumbnail(Context context, String associationId, ThumbnailKind kind) {
        new Delete().from(Thumbnail.class)
                .where()
                .and(Thumbnail_Table.idString.eq(associationId))
                .and(Thumbnail_Table.thumbnailKind.eq(kind))
                .execute();
        return true;
    }

    public Thumbnail getThumbnail(Context context, String associationId, final ThumbnailKind kind) {
        return new Select().from(Thumbnail.class)
                .where()
                .and(Thumbnail_Table.idString.eq(associationId))
                .and(Thumbnail_Table.thumbnailKind.eq(kind))
                .querySingle();
    }

    public Bitmap getThumbnailBitmap(Context context, String associationId, final ThumbnailKind kind) {
        Thumbnail thumbnail = getThumbnail(context, associationId, kind);
        if (thumbnail == null || StringUtils.isNullOrEmpty(thumbnail.getImageDataPath())) {
            return null;
        }
        return BitmapUtils.loadBitmapFromFile(thumbnail.getImageDataPath());
    }

    public void deleteThumbnail(Thumbnail thumbnail) {
        thumbnail.delete();
    }

    public List<Thumbnail> loadThumbnail(Context context, String associationId) {
        return new Select().from(Thumbnail.class).where(Thumbnail_Table.idString.eq(associationId))
                .queryList();
    }

    @Override
    public void clearMetadataCollection() {
        Delete.table(MetadataCollection.class);
    }

    @Override
    public void addMetadataCollection(Context context, MetadataCollection collection) {
        collection.save();
    }

    @Override
    public void deleteMetadataCollection(Context context, String libraryUniqueId, String metadataMD5) {
        Where<MetadataCollection> where = SQLite.delete(MetadataCollection.class)
                .where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId));
        if (StringUtils.isNotBlank(metadataMD5)) {
            where.and(MetadataCollection_Table.documentUniqueId.eq(metadataMD5));
        }
        where.execute();
    }

    @Override
    public void deleteMetadataCollection(Context context, String libraryUniqueId) {
        new Delete().from(MetadataCollection.class).where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .execute();
    }

    @Override
    public void deleteMetadataCollectionByDocId(Context context, String docId) {
        new Delete().from(MetadataCollection.class).where(MetadataCollection_Table.documentUniqueId.eq(docId))
                .execute();
    }

    @Override
    public void updateMetadataCollection(MetadataCollection collection) {
        collection.update();
    }

    @Override
    public MetadataCollection loadMetadataCollection(Context context, String libraryUniqueId, String metadataMD5) {
        return new Select().from(MetadataCollection.class)
                .where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .and(MetadataCollection_Table.documentUniqueId.eq(metadataMD5)).querySingle();
    }

    @Override
    public List<MetadataCollection> loadMetadataCollection(Context context, String libraryUniqueId) {
        return new Select().from(MetadataCollection.class)
                .where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId)).queryList();
    }

    @Override
    public MetadataCollection findMetadataCollection(Context context, String metadataMD5) {
        return new Select().from(MetadataCollection.class)
                .where(MetadataCollection_Table.documentUniqueId.eq(metadataMD5)).querySingle();
    }
}
