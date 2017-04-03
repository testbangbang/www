package com.onyx.android.sdk.data;

import android.content.Context;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 8/31/16.
 */
public class DataManager {

    private static final String TAG = DataManager.class.getSimpleName();
    private RequestManager requestManager;
    private DataProviderManager dataProviderManager = new DataProviderManager();
    private DataCacheManager dataCacheManager;
    private FileSystemManager fileSystemManager;

    public DataManager() {
        requestManager = new RequestManager();
        dataCacheManager = new DataCacheManager(getDataProviderBase());
        fileSystemManager = new FileSystemManager();
    }

    public static void init(final Context context, final List<Class<? extends DatabaseHolder>> list) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        if (list != null) {
            for (Class tClass : list) {
                builder.addDatabaseHolder(tClass);
            }
        }
        FlowManager.init(builder.build());
    }

    public static void cleanUp() {
    }

    private final Runnable generateRunnable(final BaseDataRequest request) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    request.execute(DataManager.this);
                } catch (java.lang.Exception exception) {
                    Log.e(TAG, Log.getStackTraceString(exception));
                    request.setException(exception);
                } finally {
                    request.afterExecute(DataManager.this);
                    requestManager.dumpWakelocks();
                    requestManager.removeRequest(request);
                }
            }
        };
        return runnable;
    }

    public void submit(final Context context, final BaseDataRequest request, final BaseCallback callback) {
        final String identifier = getIdentifier(request);
        if (StringUtils.isNullOrEmpty(identifier)) {
            requestManager.submitRequest(context, request, generateRunnable(request), callback);
        } else {
            requestManager.submitRequest(context, identifier, request, generateRunnable(request), callback);
        }
    }

    private final String getIdentifier(final BaseDataRequest request) {
        return request.getIdentifier();
    }

    public final RequestManager getRequestManager() {
        return requestManager;
    }

    public final DataProviderManager getDataProviderManager() {
        return dataProviderManager;
    }

    public DataProviderBase getDataProviderBase() {
        return getDataProviderManager().getDataProvider();
    }

    public final DataCacheManager getDataCacheManager() {
        return dataCacheManager;
    }

    public final FileSystemManager getFileSystemManager() {
        return fileSystemManager;
    }

    public void cacheUpdateMetaList(String oldUniqueId, String newUniqueId, List<Metadata> metadataList) {
        dataCacheManager.removeAll(oldUniqueId, metadataList);
        dataCacheManager.addAll(newUniqueId, metadataList);
    }

    public List<Metadata> getLibraryMetadataListOfAll(Context context, final QueryArgs args) {
        List<Metadata> list = new ArrayList<>();
        LibraryCache cache = dataCacheManager.getLibraryCache(args.libraryUniqueId);
        if (CollectionUtils.isNullOrEmpty(cache.getIdList())) {
            QueryArgs queryArgs = QueryBuilder.libraryAllBookQuery(args.libraryUniqueId, args.sortBy, args.order);
            list = cache.getList(context, queryArgs);
            dataCacheManager.addAll(args.libraryUniqueId, list);
            return list;
        }

        for (String id : cache.getIdList()) {
            Metadata metadata = dataCacheManager.getById(id);
            if (metadata == null) {
                continue;
            }
            list.add(metadata);
        }
        return list;
    }

    public List<Metadata> getLibraryMetadataListWithParentId(Context context, String parentId) {
        List<Metadata> list = new ArrayList<>();
        QueryArgs args = QueryBuilder.libraryAllBookQuery(parentId, SortBy.Name, SortOrder.Asc);
        list.addAll(getLibraryMetadataList(context, args));
        return list;
    }

    public List<Metadata> getLibraryMetadataList(Context context, QueryArgs queryArgs) {
        List<Metadata> list = getLibraryMetadataListOfAll(context, queryArgs);
        return dataCacheManager.getMetadataList(list, queryArgs);
    }

    public List<Metadata> getMetadataList(Context context, QueryArgs queryArgs) {
        if (dataCacheManager.isLibraryCacheReady()) {
            List<Metadata> allList = dataCacheManager.getAllMetadataList();
            return dataCacheManager.getMetadataList(allList, queryArgs);
        }

        List<Metadata> list = new ArrayList<>();
        LibraryCache cache = dataCacheManager.getLibraryCache(queryArgs.libraryUniqueId);
        if (CollectionUtils.isNullOrEmpty(cache.getIdList())) {
            list = cache.getList(context, queryArgs);
            dataCacheManager.addAll(queryArgs.libraryUniqueId, list);
            return list;
        }

        for (String id : cache.getIdList()) {
            Metadata metadata = dataCacheManager.getById(id);
            if (metadata == null) {
                continue;
            }
            list.add(metadata);
        }

        if (!CollectionUtils.isNullOrEmpty(list)) {
            if (queryArgs.filter == BookFilter.ALL && queryArgs.limit == Integer.MAX_VALUE &&
                    queryArgs.offset == 0) {
                dataCacheManager.setLibraryCacheReady(true);
            }
        }
        return list;
    }

    public List<Metadata> getMetadataListWithLimit(Context context, QueryArgs queryArgs) {
        return getDataProviderManager().getDataProvider().findMetadataByQueryArgs(context, queryArgs);
    }

    public long countMetadataList(Context context, QueryArgs queryArgs) {
        return getDataProviderManager().getDataProvider().count(context, queryArgs);
    }


    public void addToLibrary(Context context, Library library, List<Metadata> addList) {
        DataManagerHelper.addCollections(context, library, addList);
        LibraryCache cache = dataCacheManager.getLibraryCache(library.getIdString());
        if (CollectionUtils.isNullOrEmpty(cache.getIdList())) {
            dataCacheManager.removeAll(library.getParentUniqueId(), addList);
            return;
        }
        cacheUpdateMetaList(library.getParentUniqueId(), library.getIdString(), addList);
    }

    public List<Metadata> buildLibrary(Context context, Library library, QueryArgs args) {
        List<Metadata> bookList = new ArrayList<>();
        boolean isCriteriaContentEmpty = true;
        DataProviderBase providerBase = getDataProviderBase();
        if (args != null && !args.isAllSetContentEmpty()) {
            library.setQueryString(QueryArgs.toQueryString(args));
            isCriteriaContentEmpty = false;
        }

        if (!isCriteriaContentEmpty) {
            args.libraryUniqueId = library.getParentUniqueId();
            bookList = getLibraryMetadataList(context, args);
            DataManagerHelper.addCollections(context, library, bookList);
        }
        providerBase.addLibrary(library);
        return bookList;
    }

    public void clearLibrary(Context context, Library library) {
        List<Metadata> resultList = getLibraryMetadataListOfAll(context, QueryBuilder.libraryAllBookQuery(library.getIdString(), SortBy.Name, SortOrder.Desc));

        if (resultList.size() <= 0) {
            return;
        }
        dataCacheManager.addAll(library.getParentUniqueId(), resultList);
        DataManagerHelper.updateCollections(context, library.getParentUniqueId(), library.getIdString());
    }

    public void deleteLibrary(Context context, Library library) {
        DataProviderBase providerBase = getDataProviderBase();
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.deepLoadAllLibrary(libraryList, library.getIdString());
        libraryList.add(0, library);

        for (Library tmp : libraryList) {
            providerBase.deleteLibrary(tmp);
            List<Metadata> list = dataCacheManager.getMetadataList(tmp.getIdString());
            if (CollectionUtils.isNullOrEmpty(list)) {
                list = getLibraryMetadataListWithParentId(context, library.getIdString());
            } else {
                dataCacheManager.removeAll(tmp.getIdString(), list);
            }

            if (list.size() <= 0) {
                continue;
            }
            dataCacheManager.addAll(library.getParentUniqueId(), list);
            DataManagerHelper.updateCollections(context, library.getParentUniqueId(), tmp.getIdString());
        }
    }

    public void saveLibrary(Library library) {
        getDataProviderBase().addLibrary(library);
    }

    public void modifyLibrary(Context context, Library library, boolean modifyCriteria) {
        saveLibrary(library);
        if (modifyCriteria) {
            DataProviderBase providerBase = getDataProviderBase();
            List<Metadata> list = dataCacheManager.getMetadataList(library.getIdString());
            if (CollectionUtils.isNullOrEmpty(list)) {
                list.addAll(getLibraryMetadataListWithParentId(context, library.getIdString()));
            }
            if (!CollectionUtils.isNullOrEmpty(list)) {
                dataCacheManager.removeAll(library.getIdString(), list);
                dataCacheManager.addAll(library.getParentUniqueId(), list);
                DataManagerHelper.removeCollections(context, library, list);
            }
            QueryArgs criteria = QueryArgs.fromQueryString(library.getQueryString());
            criteria.libraryUniqueId = library.getParentUniqueId();
            QueryBuilder.generateQueryArgs(criteria);
            QueryBuilder.generateMetadataInQueryArgs(criteria);
            list = providerBase.findMetadataByQueryArgs(context, criteria);
            if (!CollectionUtils.isNullOrEmpty(list)) {
                dataCacheManager.addAll(library.getIdString(), list);
                dataCacheManager.removeAll(library.getParentUniqueId(), list);
                DataManagerHelper.addCollections(context, library, list);
            }
        }
    }

    public void removeFromLibrary(Context context, Library library, List<Metadata> removeList) {
        DataManagerHelper.removeCollections(context, library, removeList);
        cacheUpdateMetaList(library.getIdString(), library.getParentUniqueId(), removeList);
    }
}
