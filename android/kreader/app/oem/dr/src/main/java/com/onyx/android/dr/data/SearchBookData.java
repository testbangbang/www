package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.request.local.RequestSearchHistoryDelete;
import com.onyx.android.dr.request.local.RequestSearchHistoryInsert;
import com.onyx.android.dr.request.local.RequestSearchHistoryQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hehai on 17-8-15.
 */

public class SearchBookData {
    private List<String> hints = new ArrayList<>();
    private String type;

    public SearchBookData(String type) {
        this.type = type;
    }

    public void searchBook(final String text, final CloudContentListRequest req, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<Metadata> productResult = req.getProductResult();
                hints.clear();
                if (productResult != null && productResult.list != null) {
                    cleanData(productResult, text);
                }
                invoke(baseCallback, req, e);
            }
        });
    }

    private void cleanData(QueryResult<Metadata> productResult, String text) {
        List<Metadata> list = productResult.list;
        Iterator<Metadata> iterator = list.iterator();
        switch (type) {
            case Constants.NAME_SEARCH:
                while (iterator.hasNext()) {
                    String name = iterator.next().getName();
                    if (!name.contains(text)) {
                        iterator.remove();
                    } else {
                        hints.add(name);
                    }
                }
                break;
            case Constants.AUTHOR_SEARCH:
                while (iterator.hasNext()) {
                    String authors = iterator.next().getAuthors();
                    if (!authors.contains(text)) {
                        iterator.remove();
                    } else {
                        hints.add(authors);
                    }
                }
                break;
        }
    }

    public List<String> getHints() {
        return hints;
    }


    public void getHistory(final RequestSearchHistoryQuery req, final BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void clearHistory(final RequestSearchHistoryDelete req, final BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void insertHistory(final RequestSearchHistoryInsert req, final BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void submitRequest(final BaseDataRequest req, final BaseCallback callBack) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, callBack);
    }
}
