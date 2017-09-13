package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.SearchBookData;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.SearchBookView;
import com.onyx.android.dr.request.cloud.RequestSearchProduct;
import com.onyx.android.dr.request.local.RequestSearchHistoryDelete;
import com.onyx.android.dr.request.local.RequestSearchHistoryInsert;
import com.onyx.android.dr.request.local.RequestSearchHistoryQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-8-15.
 */

public class SearchBookPresenter {
    private SearchBookView searchBookView;
    private SearchBookData searchBookData;

    public SearchBookPresenter(SearchBookView searchBookView) {
        this.searchBookView = searchBookView;
        this.searchBookData = new SearchBookData();
    }

    public void getHistory() {
        final RequestSearchHistoryQuery req = new RequestSearchHistoryQuery();
        searchBookData.getHistory(req, new BaseCallback() {

            @Override
            public void done(BaseRequest request, Throwable e) {
                searchBookView.setHistory(req.history);
            }
        });

    }

    public void searchBook(LibraryDataHolder holder, String text, final boolean showResult) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.query = text;
        queryArgs.filter = BookFilter.SEARCH;
        queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
        queryArgs = holder.getCloudViewInfo().generateQueryArgs(queryArgs);
        queryArgs.conditionGroup.and(CloudMetadata_Table.nativeAbsolutePath.isNotNull());
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        final CloudContentListRequest req = new CloudContentListRequest(queryArgs);
        searchBookData.searchLocalBook(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                countDownLatch.countDown();
                if (countDownLatch.getCount() == 0) {
                    setResult(showResult);
                }
            }
        });

        final RequestSearchProduct reqCloud = new RequestSearchProduct(text);
        searchBookData.searchCloudBook(reqCloud, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                countDownLatch.countDown();
                if (countDownLatch.getCount() == 0) {
                    setResult(showResult);
                }
            }
        });
    }

    private void setResult(boolean showResult) {
        if (showResult) {
            searchBookView.setResult(searchBookData.getResult());
        } else {
            searchBookView.setHint(searchBookData.getHints());
        }
    }

    public void insertHistory(String text) {
        RequestSearchHistoryInsert req = new RequestSearchHistoryInsert(text);
        searchBookData.insertHistory(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public void clearHistory() {
        RequestSearchHistoryDelete req = new RequestSearchHistoryDelete();
        searchBookData.clearHistory(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getHistory();
            }
        });
    }
}
