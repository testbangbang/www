package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.request.cloud.v2.CloudChildLibraryListLoadRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-8-4.
 */

public class BookshelfData {
    private Map<String, List<Metadata>> languageCategoryMap = new HashMap<>();
    private List<Metadata> chineseList = new ArrayList<>();
    private List<Metadata> englishList = new ArrayList<>();
    private List<Metadata> smallList = new ArrayList<>();

    public void getLibraryBooks(final CloudContentListRequest req, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                chineseList.clear();
                englishList.clear();
                smallList.clear();
                languageCategoryMap.clear();
                QueryResult<Metadata> productResult = req.getProductResult();
                if (productResult != null && productResult.list != null) {
                    List<Metadata> list = productResult.list;
                    for (Metadata metadata : list) {
                        if (Constants.CHINESE.equals(metadata.getLanguage())) {
                            chineseList.add(metadata);
                        } else if (Constants.ENGLISH.equals(metadata.getLanguage())) {
                            englishList.add(metadata);
                        } else {
                            smallList.add(metadata);
                        }
                    }
                    if (chineseList.size() > 0) {
                        languageCategoryMap.put(Constants.CHINESE, chineseList);
                    }
                    if (englishList.size() > 0) {
                        languageCategoryMap.put(Constants.ENGLISH, englishList);
                    }
                    if (smallList.size() > 0) {
                        languageCategoryMap.put(Constants.SMALL_LANGUAGE, smallList);
                    }
                }
                invoke(baseCallback, request, e);
            }
        });
    }

    public Map<String, List<Metadata>> getLanguageCategoryMap() {
        return languageCategoryMap;
    }

    public void getLanguageBooks(LibraryDataHolder holder, String language, List<Library> libraryList, final BaseCallback baseCallback) {
        final CountDownLatch countDownLatch = new CountDownLatch(libraryList.size());
        languageCategoryMap.clear();
        for (final Library library : libraryList) {
            QueryArgs queryArgs = holder.getCloudViewInfo().buildLibraryQuery(library.getIdString());
            queryArgs.conditionGroup.and(CloudMetadata_Table.language.eq(language)).and(CloudMetadata_Table.nativeAbsolutePath.isNotNull());
            queryArgs.libraryUniqueId = library.getIdString();
            queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
            final CloudContentListRequest req = new CloudContentListRequest(queryArgs);
            DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    QueryResult<Metadata> result = req.getProductResult();
                    if (result != null && result.list != null) {
                        languageCategoryMap.put(library.getName(), result.list);
                    }
                    countDownLatch.countDown();
                    if (countDownLatch.getCount() == 0) {
                        invoke(baseCallback, request, e);
                    }
                }
            });
        }
    }

    public void getLibraryList(CloudChildLibraryListLoadRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }
}
