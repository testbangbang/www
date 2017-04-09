package com.onyx.android.sdk.data.provider;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
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

    public Metadata findMetadataByHashTag(final Context context, final String path, String hashTag) {
        Metadata metadata = null;
        try {
            metadata = new Select().from(Metadata.class).where(Metadata_Table.nativeAbsolutePath.eq(path)).querySingle();
            if (metadata != null) {
                return metadata;
            }
            if (StringUtils.isNullOrEmpty(hashTag)) {
                hashTag = FileUtils.computeMD5(new File(path));
            }
            metadata = new Select().from(Metadata.class).where(Metadata_Table.hashTag.eq(hashTag)).querySingle();
            return metadata;
        } catch (Exception e) {
        }
        return metadata;
    }

    public Metadata loadMetadataByHashTag(final Context context, final String path, String md5) {
        Metadata metadata = findMetadataByHashTag(context, path, md5);
        if (metadata == null) {
            metadata = new Metadata();
        }
        return metadata;
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
            Metadata document;
            final Metadata options = findMetadataByHashTag(context, path, md5);
            if (options == null) {
                document = new Metadata();
                document.setHashTag(md5);
            } else {
                document = options;
            }
            document.setExtraAttributes(json);
            if (options == null) {
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
    public void clearThumbnail() {
        Delete.table(Thumbnail.class);
    }

    @Override
    public List<Thumbnail> addThumbnail(Context context, String sourceMD5, Bitmap saveBitmap) {
        List<Thumbnail> list = new ArrayList<>();
        for (ThumbnailKind tk : ThumbnailKind.values()) {
            Thumbnail thumbnail = new Thumbnail();
            thumbnail.setSourceMD5(sourceMD5);
            thumbnail.setThumbnailKind(tk);
            ThumbnailUtils.saveThumbnailBitmap(context, thumbnail, saveBitmap);
            list.add(thumbnail);
        }
        return list;
    }

    @Override
    public void updateThumbnail(Thumbnail thumbnail) {
        thumbnail.update();
    }

    @Override
    public void deleteThumbnail(Thumbnail thumbnail) {
        thumbnail.delete();
    }

    @Override
    public List<Thumbnail> loadThumbnail(Context context, String sourceMd5) {
        return new Select().from(Thumbnail.class).where(Thumbnail_Table.sourceMD5.eq(sourceMd5))
                .queryList();
    }

    @Override
    public Thumbnail loadThumbnail(Context context, String sourceMd5, ThumbnailKind kind) {
        return new Select().from(Thumbnail.class).where(Thumbnail_Table.sourceMD5.eq(sourceMd5))
                .and(Thumbnail_Table.thumbnailKind.eq(kind)).querySingle();
    }

    @Override
    public Bitmap loadThumbnailBitmap(Context context, String sourceMd5, ThumbnailKind kind) {
        return ThumbnailUtils.getThumbnailBitmap(context, sourceMd5, kind.toString());
    }

    @Override
    public Bitmap loadThumbnailBitmap(Context context, Thumbnail thumbnail) {
        return ThumbnailUtils.getThumbnailBitmap(context, thumbnail);
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

}
