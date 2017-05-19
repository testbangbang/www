package com.onyx.android.sdk.data.provider;

import android.content.Context;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.converter.QueryArgsFilter;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.data.model.CloudLibrary;
import com.onyx.android.sdk.data.model.CloudLibrary_Table;
import com.onyx.android.sdk.data.model.CloudMetadata;
import com.onyx.android.sdk.data.model.CloudMetadataCollection;
import com.onyx.android.sdk.data.model.CloudMetadataCollection_Table;
import com.onyx.android.sdk.data.model.CloudMetadata_Table;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.MetadataCollection_Table;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.model.Thumbnail_Table;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.data.v1.ContentService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/5/5.
 */

public class CloudDataProvider implements DataProviderBase {

    private CloudConf conf;

    public CloudDataProvider(CloudConf conf) {
        this.conf = conf;
    }

    @Override
    public void clearMetadata() {
        Delete.table(CloudMetadata.class);
    }

    @Override
    public void saveMetadata(Context context, Metadata metadata) {
        metadata.beforeSave();
        Metadata findMeta = findMetadataByCloudId(metadata.getCloudId());
        metadata.setId(findMeta.getId());
        metadata.save();
    }

    private Metadata findMetadataByCloudId(String cloudId) {
        Metadata metadata = new Select().from(CloudMetadata.class).where(CloudMetadata_Table.cloudId.eq(cloudId)).querySingle();
        return MetadataUtils.ensureObject(metadata);
    }

    private List<Metadata> findMetadataFromLocalByQueryArgs(Context context, QueryArgs queryArgs) {
        if (queryArgs.conditionGroup != null) {
            Where<CloudMetadata> where = new Select(queryArgs.propertyList.toArray(new IProperty[0])).from(CloudMetadata.class)
                    .where(queryArgs.conditionGroup);
            for (OrderBy orderBy : queryArgs.orderByList) {
                where.orderBy(orderBy);
            }
            List<CloudMetadata> cloudList = where.offset(queryArgs.offset).limit(queryArgs.limit).queryList();
            List<Metadata> list = new ArrayList<>();
            for (CloudMetadata cloudMetadata : cloudList) {
                list.add(cloudMetadata);
            }
            return list;
        }
        return new ArrayList<>();
    }

    private long countMetadataFromLocal(Context context, QueryArgs queryArgs) {
        return new Select(Method.count()).from(CloudMetadata.class).where(queryArgs.conditionGroup).count();
    }

    private QueryResult<Metadata> fetchFromLocal(Context context, QueryArgs queryArgs) {
        QueryResult<Metadata> result = new QueryResult<>();
        result.list = findMetadataFromLocalByQueryArgs(context, queryArgs);
        result.count = countMetadataFromLocal(context, queryArgs);
        result.fetchSource = Metadata.FetchSource.LOCAL;
        return result;
    }

    private QueryResult<Metadata> fetchFromCloud(Context context, QueryArgs queryArgs) {
        QueryResult<Metadata> result = new QueryResult<>();
        try {
            Response<ProductResult<CloudMetadata>> response = executeCall(getContentService().loadBookList(
                    queryArgs.libraryUniqueId, JSON.toJSONString(queryArgs, new QueryArgsFilter())));
            if (response.isSuccessful()) {
                result.list = new ArrayList<>();
                for (Metadata metadata : response.body().list) {
                    result.list.add(metadata);
                }
                result.count = response.body().count;
                result.fetchSource = Metadata.FetchSource.CLOUD;
            }
        } catch (Exception e) {
            result = fetchFromLocal(context, queryArgs);
        }
        return result;
    }

    @Override
    public QueryResult<Metadata> findMetadataResultByQueryArgs(Context context, QueryArgs queryArgs) {
        QueryResult<Metadata> result;
        if (NetworkUtil.isWifiConnected(context)) {
            result = fetchFromCloud(context, queryArgs);
        } else {
            result = fetchFromLocal(context, queryArgs);
        }
        return result;
    }

    @Override
    public List<Metadata> findMetadataByQueryArgs(Context context, QueryArgs queryArgs) {
        QueryResult<Metadata> result = findMetadataResultByQueryArgs(context, queryArgs);
        return result.list;
    }

    @Override
    public Metadata findMetadataByIdString(Context context, String idString) {
        return null;
    }

    @Override
    public Metadata findMetadataByPath(Context context, String path) {
        return null;
    }

    @Override
    public Metadata findMetadataByHashTag(Context context, String path, String hashTag) {
        return null;
    }

    @Override
    public long count(Context context, QueryArgs queryArgs) {
        long count;
        if (NetworkUtil.isWifiConnected(context)) {
            count = findMetadataResultByQueryArgs(context, queryArgs).count;
        } else {
            count = countMetadataFromLocal(context, queryArgs);
        }
        return count;
    }

    @Override
    public void removeMetadata(Context context, Metadata metadata) {
        metadata.delete();
    }

    @Override
    public boolean saveDocumentOptions(Context context, String path, String associationId, String json) {
        return false;
    }

    @Override
    public List<Annotation> loadAnnotations(String application, String associationId, int pageNumber, OrderBy orderBy) {
        return null;
    }

    @Override
    public List<Annotation> loadAnnotations(String application, String associationId, OrderBy orderBy) {
        return null;
    }

    @Override
    public void addAnnotation(Annotation annotation) {

    }

    @Override
    public void updateAnnotation(Annotation annotation) {

    }

    @Override
    public void deleteAnnotation(Annotation annotation) {

    }

    @Override
    public Bookmark loadBookmark(String application, String associationId, int pageNumber) {
        return null;
    }

    @Override
    public List<Bookmark> loadBookmarks(String application, String associationId, OrderBy orderBy) {
        return null;
    }

    @Override
    public void addBookmark(Bookmark bookmark) {

    }

    @Override
    public void deleteBookmark(Bookmark bookmark) {

    }

    @Override
    public Library loadLibrary(String uniqueId) {
        return new Select().from(CloudLibrary.class)
                .where()
                .and(CloudLibrary_Table.idString.eq(uniqueId))
                .querySingle();
    }

    @Override
    public List<Library> loadAllLibrary(String parentId) {
        return null;
    }

    @Override
    public void addLibrary(Library library) {
        Library findLibrary = loadLibrary(library.getIdString());
        if (findLibrary != null) {
            library.setId(library.getId());
        }
        library.save();
    }

    @Override
    public void updateLibrary(Library library) {

    }

    @Override
    public void deleteLibrary(Library library) {

    }

    @Override
    public void clearLibrary() {

    }

    @Override
    public void clearThumbnails() {

    }

    @Override
    public void saveThumbnailEntry(Context context, Thumbnail thumbnail) {
        Thumbnail getThumbnail = getThumbnailEntry(context, thumbnail.getIdString(), thumbnail.getThumbnailKind());
        if (getThumbnail != null) {
            thumbnail.setId(getThumbnail.getId());
        }
        thumbnail.save();
    }

    @Override
    public Thumbnail getThumbnailEntry(Context context, String associationId, OnyxThumbnail.ThumbnailKind kind) {
        return new Select().from(Thumbnail.class)
                .where()
                .and(Thumbnail_Table.idString.eq(associationId))
                .and(Thumbnail_Table.thumbnailKind.eq(kind))
                .querySingle();
    }

    @Override
    public void deleteThumbnailEntry(Thumbnail thumbnail) {

    }

    @Override
    public boolean saveThumbnailBitmap(Context context, String associationId, OnyxThumbnail.ThumbnailKind kind, Bitmap saveBitmap) {
        return false;
    }

    @Override
    public Bitmap getThumbnailBitmap(Context context, String associationId, OnyxThumbnail.ThumbnailKind kind) {
        return null;
    }

    @Override
    public boolean removeThumbnailBitmap(Context context, String associationId, OnyxThumbnail.ThumbnailKind kind) {
        return false;
    }

    @Override
    public void clearMetadataCollection() {

    }

    @Override
    public void addMetadataCollection(Context context, MetadataCollection collection) {
        MetadataCollection findCollection = loadMetadataCollection(context, collection.getLibraryUniqueId(),
                collection.getDocumentUniqueId());
        if (findCollection != null) {
            collection.setId(findCollection.getId());
        }
        collection.save();
    }

    @Override
    public void deleteMetadataCollection(Context context, String libraryUniqueId, String associationId) {
        new Delete().from(CloudMetadataCollection.class)
                .where(CloudMetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .and(CloudMetadataCollection_Table.documentUniqueId.eq(associationId))
                .execute();
    }

    @Override
    public void deleteMetadataCollection(Context context, String libraryUniqueId) {
        new Delete().from(CloudMetadataCollection.class)
                .where(CloudMetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .execute();
    }

    @Override
    public void deleteMetadataCollectionByDocId(Context context, String docId) {
        new Delete().from(CloudMetadataCollection.class)
                .where(CloudMetadataCollection_Table.documentUniqueId.eq(docId))
                .execute();
    }

    @Override
    public void updateMetadataCollection(MetadataCollection collection) {
        collection.update();
    }

    @Override
    public MetadataCollection loadMetadataCollection(Context context, String libraryUniqueId, String associationId) {
        return new Select().from(CloudMetadataCollection.class)
                .where(CloudMetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .and(CloudMetadataCollection_Table.documentUniqueId.eq(associationId)).querySingle();
    }

    @Override
    public List<MetadataCollection> loadMetadataCollection(Context context, String libraryUniqueId) {
        List<CloudMetadataCollection> list = new Select().from(CloudMetadataCollection.class)
                .where(CloudMetadataCollection_Table.libraryUniqueId.eq(libraryUniqueId))
                .queryList();
        if (CollectionUtils.isNullOrEmpty(list)) {
            return new ArrayList<>();
        }
        List<MetadataCollection> collectionList = new ArrayList<>();
        for (CloudMetadataCollection collection : list) {
            collectionList.add(collection);
        }
        return collectionList;
    }

    @Override
    public MetadataCollection findMetadataCollection(Context context, String associationId) {
        return new Select().from(CloudMetadataCollection.class)
                .where(CloudMetadataCollection_Table.documentUniqueId.eq(associationId))
                .querySingle();
    }

    private <T> Response<T> executeCall(Call<T> call) throws Exception {
        Response<T> response = call.execute();
        if (!response.isSuccessful()) {
            String errorBody = response.errorBody().string();
            throw new Exception(errorBody);
        }
        return response;
    }

    private ContentService getContentService() {
        return ServiceFactory.getContentService(conf.getApiBase());
    }
}
