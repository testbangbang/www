package com.onyx.android.sdk.data.provider;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail.ThumbnailKind;
import com.onyx.android.sdk.data.db.table.OnyxAnnotationProvider;
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
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.data.model.SearchHistory_Table;
import com.onyx.android.sdk.data.model.StatisticalData;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.model.Thumbnail_Table;
import com.onyx.android.sdk.data.model.StatisticalData_Table;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.sql.language.property.Property;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuzeng on 8/27/16.
 */
public class LocalDataProvider implements DataProviderBase {

    public void clearMetadata() {
        Delete.table(Metadata.class);
    }

    public Metadata findMetadataByIdString(final Context context, final String idString) {
        Metadata metadata = null;
        try {
            metadata = new Select().from(Metadata.class).where(Metadata_Table.idString.eq(idString)).querySingle();
        } catch (Exception e) {
        } finally {
            return MetadataUtils.ensureObject(metadata);
        }
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

    public Metadata findMetadataByCloudId(String cloudId) {
        Metadata metadata = null;
        try {
            metadata = new Select().from(Metadata.class).where(Metadata_Table.cloudId.eq(cloudId)).querySingle();
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
            metadata = new Select().from(Metadata.class).where()
                    .or(Metadata_Table.hashTag.eq(hashTag))
                    .or(Metadata_Table.nativeAbsolutePath.eq(path)).querySingle();
        } catch (Exception e) {
        } finally {
            return MetadataUtils.ensureObject(metadata);
        }
    }

    @Override
    public QueryResult<Metadata> findMetadataResultByQueryArgs(Context context, QueryArgs queryArgs) {
        QueryResult<Metadata> result = new QueryResult<>();
        result.list = findMetadataByQueryArgs(context, queryArgs);
        result.count = count(context, queryArgs);
        return result;
    }

    public List<Metadata> findMetadataByQueryArgs(final Context context, final QueryArgs queryArgs) {
        if (queryArgs.conditionGroup != null) {
            Where<Metadata> where = new Select(queryArgs.propertyList.toArray(new IProperty[0])).from(Metadata.class)
                    .where(queryArgs.conditionGroup);
            for (OrderBy orderBy : queryArgs.orderByList) {
                where.orderBy(orderBy.collate(Collate.LOCALIZED));
            }
            return where.offset(queryArgs.offset).limit(queryArgs.limit).queryList();
        }
        return new ArrayList<>();
    }

    public List<Metadata> findCloudMetadata() {
        List<Metadata> queryList = new Select().from(Metadata.class).where(Metadata_Table.cloudId.isNotNull()).queryList();
        return queryList;
    }

    public long count(final Context context, final QueryArgs queryArgs) {
        return new Select(Method.count()).from(Metadata.class).where(queryArgs.conditionGroup).count();
    }

    public void saveMetadata(final Context context, final Metadata metadata) {
        metadata.save();
    }

    public void updateMetadata(final Context context, final Metadata metadata) {
        metadata.update();
    }

    public void removeMetadata(final Context context, final Metadata metadata) {
        metadata.delete();
    }

    public boolean saveDocumentOptions(final Context context, final String path, String md5, final String json) {
        try {
            final Metadata document = findMetadataByHashTag(context, path, md5);
            document.setExtraAttributes(json);
            if (!document.hasValidId()) {
                document.setNativeAbsolutePath(path);
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


    public final List<Annotation> loadAnnotations(final String application, final String associationId, final int pageNumber, final OrderBy orderBy) {
        return new Select().from(Annotation.class).where(Annotation_Table.idString.eq(associationId))
                .and(Annotation_Table.application.eq(application))
                .and(Annotation_Table.pageNumber.eq(pageNumber))
                .orderBy(orderBy)
                .queryList();
    }

    public final List<Annotation> loadAnnotations(final String application, final String associationId, final OrderBy orderBy) {
        return new Select().from(Annotation.class).where(Annotation_Table.idString.eq(associationId))
                .and(Annotation_Table.application.eq(application))
                .orderBy(orderBy)
                .queryList();
    }

    public final List<Annotation> loadKeyAnnotations(final String application, final String key, final OrderBy orderBy) {
        return new Select().from(Annotation.class).where(Annotation_Table.key.eq(key))
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

    public final Bookmark loadBookmark(final String application, final String associationId, final int pageNumber) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.idString.eq(associationId))
                .and(Bookmark_Table.application.eq(application))
                .and(Bookmark_Table.pageNumber.eq(pageNumber))
                .querySingle();
    }

    public final List<Bookmark> loadBookmarks(final String application, final String associationId, final OrderBy orderBy) {
        return new Select().from(Bookmark.class).where(Bookmark_Table.idString.eq(associationId))
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

    private Operator getNullOrEqualCondition(Property<String> property, String compare) {
        return StringUtils.isNullOrEmpty(compare) ? property.isNull() : property.eq(compare);
    }

    private Operator getNotNullOrEqualCondition(Property<String> property, String compare) {
        return StringUtils.isNullOrEmpty(compare) ? property.isNotNull() : property.eq(compare);
    }

    @Override
    public Library loadLibrary(String uniqueId) {
        return new Select().from(Library.class).where(Library_Table.idString.eq(uniqueId)).querySingle();
    }

    @Override
    public QueryResult<Library> fetchAllLibrary(String paretId, QueryArgs queryArgs) {
        QueryResult<Library> result = new QueryResult<>();
        result.list = loadAllLibrary(queryArgs.libraryUniqueId, queryArgs);
        result.count = CollectionUtils.getSize(result.list);
        return result;
    }

    @Override
    public List<Library> loadAllLibrary(String parentId, QueryArgs queryArgs) {
        Operator condition = getNullOrEqualCondition(Library_Table.parentUniqueId, parentId);
        return new Select().from(Library.class).where(condition).offset(queryArgs.offset).limit(queryArgs.limit).orderBy(OrderBy.fromProperty(SearchHistory_Table.updatedAt).descending()).queryList();
    }

    @Override
    public void addLibrary(Library library) {
        library.save();
    }

    @Override
    public void updateLibrary(Library library) {
        library.beforeSave();
        library.update();
    }

    @Override
    public void deleteLibrary(Library library) {
        library.delete();
    }

    @Override
    public void deleteLibrary(String libraryUniqueId) {
        Operator condition = getNullOrEqualCondition(Library_Table.idString, libraryUniqueId);
        new Delete().from(Library.class).where(condition).query();
    }

    @Override
    public void clearLibrary() {
        Delete.table(Library.class);
    }

    @Override
    public void clearThumbnails() {
        Delete.table(Thumbnail.class);
    }

    @Override
    public void saveThumbnailEntry(Context context, Thumbnail thumbnail) {
        thumbnail.save();
    }

    public Thumbnail getThumbnailEntry(Context context, String associationId, final ThumbnailKind kind) {
        return new Select().from(Thumbnail.class)
                .where()
                .and(Thumbnail_Table.idString.eq(associationId))
                .and(Thumbnail_Table.thumbnailKind.eq(kind))
                .querySingle();
    }

    public Thumbnail getThumbnailEntryByOriginContentPath(Context context, String originContentPath, final ThumbnailKind kind) {
        return new Select().from(Thumbnail.class)
                .where()
                .and(Thumbnail_Table.originContentPath.eq(originContentPath))
                .and(Thumbnail_Table.thumbnailKind.eq(kind))
                .querySingle();
    }

    public void deleteThumbnailEntry(Thumbnail thumbnail) {
        thumbnail.delete();
    }

    @Override
    public boolean saveThumbnailBitmap(Context context, String associationId, ThumbnailKind kind, final Bitmap saveBitmap) {
        return false;
    }

    public boolean removeThumbnailBitmap(Context context, String associationId, ThumbnailKind kind) {
        new Delete().from(Thumbnail.class)
                .where()
                .and(Thumbnail_Table.idString.eq(associationId))
                .and(Thumbnail_Table.thumbnailKind.eq(kind))
                .execute();
        return true;
    }

    public Bitmap getThumbnailBitmap(Context context, String associationId, final ThumbnailKind kind) {
        Thumbnail thumbnail = getThumbnailEntry(context, associationId, kind);
        if (thumbnail == null || StringUtils.isNullOrEmpty(thumbnail.getImageDataPath())) {
            return null;
        }
        return BitmapUtils.loadBitmapFromFile(thumbnail.getImageDataPath());
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
    public void deleteMetadataCollection(Context context, String libraryUniqueId, String associationId) {
        if (StringUtils.isNullOrEmpty(associationId)) {
            return;
        }
        Where<MetadataCollection> where = SQLite.delete(MetadataCollection.class)
                .where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId)).and(MetadataCollection_Table.documentUniqueId.eq(associationId));
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
    public MetadataCollection loadMetadataCollection(Context context, String libraryUniqueId, String associationId) {
        return new Select().from(MetadataCollection.class)
                .where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .and(MetadataCollection_Table.documentUniqueId.eq(associationId)).querySingle();
    }

    @Override
    public List<MetadataCollection> loadMetadataCollection(Context context, String libraryUniqueId) {
        return new Select().from(MetadataCollection.class)
                .where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId)).queryList();
    }

    @Override
    public MetadataCollection findMetadataCollection(Context context, String associationId) {
        return new Select().from(MetadataCollection.class)
                .where(MetadataCollection_Table.documentUniqueId.eq(associationId)).querySingle();
    }

    @Override
    public Library findLibraryByName(Context appContext, String name) {
        return new Select().from(Library.class).where(Library_Table.name.eq(name)).querySingle();
    }

    @Override
    public long libraryMetadataCount(String libraryUniqueId) {
        return new Select().from(MetadataCollection.class).where(MetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId)).queryList().size();
    }

    @Override
    public List<SearchHistory> loadSearchHistory(int limit) {
        return new Select().from(SearchHistory.class).orderBy(OrderBy.fromProperty(SearchHistory_Table.createdAt).descending()).limit(limit).queryList();
    }

    @Override
    public void saveSearchHistory(Context context, SearchHistory searchHistory) {
        SearchHistory single = new Select().from(SearchHistory.class).where(SearchHistory_Table.content.eq(searchHistory.getContent())).querySingle();
        if (single == null) {
            searchHistory.save();
        } else {
            single.setCreatedAt(new Date());
            single.update();
        }
    }

    @Override
    public void clearSearchHistory() {
        new Delete().from(SearchHistory.class).query();
    }

    @Override
    public long libraryCount(String parentUniqueId) {
        Operator condition = getNullOrEqualCondition(Library_Table.parentUniqueId, parentUniqueId);
        return new Select(Method.count()).from(Library.class).where(condition).count();
    }

    @Override
    public StatisticalData findStatisticalDataByCloudId(String cloudId) {
        StatisticalData statisticalData = new Select().from(StatisticalData.class).where(StatisticalData_Table.cloudId.eq(cloudId)).querySingle();
        return statisticalData;
    }

    @Override
    public List<StatisticalData> findAllStatistics() {
        List<StatisticalData> list = new Select().from(StatisticalData.class).where(StatisticalData_Table.cloudId.isNotNull()).queryList();
        return list;
    }

    @Override
    public void saveStatisticData(StatisticalData data) {
        StatisticalData statisticalData = new Select().from(StatisticalData.class).where(StatisticalData_Table.cloudId.eq(data.cloudId)).querySingle();
        if (statisticalData == null) {
            data.save();
        } else {
            statisticalData.endReadTime = data.endReadTime;
            statisticalData.startReadTime = data.startReadTime;
            statisticalData.length = data.length;
            statisticalData.update();
        }
    }

    @Override
    public void deleteStatisticDataByCloudId(String cloudId) {
        new Delete().from(StatisticalData.class).where(StatisticalData_Table.cloudId.eq(cloudId)).execute();
    }
}
