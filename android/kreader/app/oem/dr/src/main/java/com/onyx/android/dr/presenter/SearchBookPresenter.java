package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.SearchBookData;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.SearchBookView;
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
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

/**
 * Created by hehai on 17-8-15.
 */

public class SearchBookPresenter {
    private SearchBookView searchBookView;
    private SearchBookData searchBookData;

    public SearchBookPresenter(SearchBookView searchBookView, String type) {
        this.searchBookView = searchBookView;
        this.searchBookData = new SearchBookData(type);
    }

    public void getHistory(String type) {
        final RequestSearchHistoryQuery req = new RequestSearchHistoryQuery(type);
        searchBookData.getHistory(req, new BaseCallback() {

            @Override
            public void done(BaseRequest request, Throwable e) {
                searchBookView.setHistory(req.history);
            }
        });

    }

    public void searchBook(LibraryDataHolder holder, String text, final boolean showResult) {
        QueryArgs queryArgs = holder.getCloudViewInfo().libraryQuery();
        queryArgs.query = text;
        queryArgs.filter = BookFilter.SEARCH;
        queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
        queryArgs.conditionGroup = ConditionGroup.clause().and(CloudMetadata_Table.nativeAbsolutePath.isNotNull());
        final CloudContentListRequest req = new CloudContentListRequest(queryArgs);
        searchBookData.searchBook(text, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (showResult) {
                    searchBookView.setResult(req.getProductResult());
                } else {
                    searchBookView.setHint(searchBookData.getHints());
                }
            }
        });
    }

    public void insertHistory(String text, String type) {
        RequestSearchHistoryInsert req = new RequestSearchHistoryInsert(text, type);
        searchBookData.insertHistory(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }

    public void clearHistory(final String type) {
        RequestSearchHistoryDelete req = new RequestSearchHistoryDelete(type);
        searchBookData.clearHistory(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getHistory(type);
            }
        });
    }
}
