package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.request.cloud.RequestAddProduct;
import com.onyx.android.dr.request.cloud.RequestCreateOrders;
import com.onyx.android.dr.request.cloud.RequestGetProducts;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.request.cloud.v2.CloudChildLibraryListLoadRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-8-2.
 */

public class EBookStoreData {
    private List<Library> libraryList;
    private List<Metadata> books = new ArrayList<>();
    private List<String> languageList = new ArrayList<>();

    public void getRootLibraryList(final CloudChildLibraryListLoadRequest req, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                libraryList = req.getLibraryList();
                invoke(baseCallback, request, e);
            }
        });
    }

    public void getLanguageBooks(LibraryDataHolder holder, final String language, final BaseCallback baseCallback) {
        books.clear();
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        final CountDownLatch countDownLatch = new CountDownLatch(libraryList.size());
        for (final Library library : libraryList) {
            QueryArgs queryArgs = holder.getCloudViewInfo().buildLibraryQuery(library.getIdString());
            queryArgs.conditionGroup.and(CloudMetadata_Table.language.eq(language));
            queryArgs.limit = 50;
            queryArgs.fetchPolicy = FetchPolicy.CLOUD_ONLY;
            final CloudContentListRequest req = new CloudContentListRequest(queryArgs);
            req.setLoadThumbnail(false);
            DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    QueryResult<Metadata> result = req.getProductResult();
                    if (result != null && result.list != null && result.list.size() > 0) {
                        for (Metadata metadata : result.list) {
                            if (language.equals(metadata.getLanguage())) {
                                books.add(metadata);
                            }
                        }
                    }
                    countDownLatch.countDown();
                    if (countDownLatch.getCount() == 0) {
                        invoke(baseCallback, request, e);
                    }
                }
            });
        }
    }

    public List<Metadata> getBooks() {
        return books;
    }

    public List<String> getLanguageList() {
        languageList.clear();
        languageList.add(Constants.CHINESE);
        languageList.add(Constants.ENGLISH);
        languageList.add(Constants.SMALL_LANGUAGE);
        return languageList;
    }

    public void createOrder(RequestCreateOrders req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void addToCart(RequestAddProduct req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void getProducts(final RequestGetProducts req, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
