package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.MainFragmentData;
import com.onyx.android.dr.interfaces.MainFragmentView;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.CloudMetadata_Table;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.data.db.MetadataRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by hehai on 17-8-4.
 */

public class MainViewFragmentPresenter {
    private MainFragmentView fragmentView;
    private MainFragmentData mainFragmentData;

    public MainViewFragmentPresenter(MainFragmentView fragmentView) {
        this.fragmentView = fragmentView;
        mainFragmentData = new MainFragmentData();
    }

    public void loadData(String bookshelf) {
        QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.CreationTime, SortOrder.Desc);
        queryArgs.conditionGroup.and(CloudMetadata_Table.nativeAbsolutePath.isNotNull());
        if (StringUtils.isNotBlank(bookshelf)) {
            queryArgs.conditionGroup.and(CloudMetadata_Table.language.eq(bookshelf));
        }
        queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
        queryArgs.sortBy = SortBy.RecentlyRead;
        final CloudContentListRequest req = new CloudContentListRequest(queryArgs);
        mainFragmentData.loadData(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<Metadata> productResult = req.getProductResult();
                fragmentView.setDatas(productResult);
            }
        });
    }

    public void getNowReading() {
        final QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.RecentlyRead, SortOrder.Desc);
        final MetadataRequest req = new MetadataRequest(queryArgs);
        mainFragmentData.getNowReading(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<Metadata> queryResult = new QueryResult<>();
                queryResult.list = req.getList();
                queryResult.count = req.getList().size();
                fragmentView.setNowReading(queryResult);
            }
        });
    }
}
