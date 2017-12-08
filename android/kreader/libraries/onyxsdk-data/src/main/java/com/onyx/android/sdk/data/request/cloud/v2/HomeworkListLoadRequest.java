package com.onyx.android.sdk.data.request.cloud.v2;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.QueryBase;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.converter.QueryArgsFilter;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.Homework;
import com.onyx.android.sdk.data.model.v2.Homework_Table;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.data.utils.RetrofitUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2017/11/2.
 */
public class HomeworkListLoadRequest extends BaseCloudRequest {
    private static final String TAG = "HomeworkListLoadRequest";

    private QueryBase queryArgs;
    private QueryResult<Homework> queryResult = new QueryResult<>();

    public HomeworkListLoadRequest(QueryBase queryArgs) {
        this.queryArgs = queryArgs;
    }

    public QueryResult<Homework> getQueryResult() {
        return queryResult;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        queryResult = fetchData(parent);
    }

    private QueryResult<Homework> fetchData(CloudManager parent) {
        if(!FetchPolicy.isCloudPartPolicy(queryArgs.fetchPolicy)) {
            return fetchFromLocal();
        }
        QueryResult<Homework> result = new QueryResult<>();
        try {
            result = fetchFromCloud(parent);
            if (isSaveToLocal()) {
                saveToLocal(result);
            }
        } catch (Exception e) {
            if (!FetchPolicy.isCloudOnlyPolicy(queryArgs.fetchPolicy)) {
                result = fetchFromLocal();
            } else {
                result.setException(ContentException.createException(e));
            }
        }
        return result;
    }

    private QueryResult<Homework> fetchFromCloud(CloudManager parent) throws Exception {
        QueryResult<Homework> result = new QueryResult<>();
        String param = JSON.toJSONString(queryArgs, new QueryArgsFilter() {
            @Override
            protected String getString(SortBy sortBy) {
                return getName(Homework_Table.updatedAt);
            }
        });
        Response<ResponseBody> response = RetrofitUtils.executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getMySource(param));
        if (response.isSuccessful()) {
            String content = response.body().string();
            result = JSON.parseObject(content, new TypeReference<QueryResult<Homework>>() {
            });
            result.fetchSource = Metadata.FetchSource.CLOUD;
        }
        return result;
    }

    private long fetchLocalDataCount(QueryBase queryArgs) {
        try {
            return StoreUtils.queryDataCount(Homework.class, queryArgs.conditionGroup);
        } catch (Exception e) {
            return 0;
        }
    }

    private QueryResult<Homework> fetchFromLocal() {
        QueryResult<Homework> result = new QueryResult<>();
        long totalCount = fetchLocalDataCount(queryArgs);
        result.list = SQLite.select()
                .from(Homework.class)
                .where(queryArgs.conditionGroup)
                .limit(queryArgs.limit)
                .offset(queryArgs.offset)
                .orderBy(getOrderBy(queryArgs))
                .queryList();
        long listCount = CollectionUtils.getSize(result.list);
        result.count = totalCount >= listCount ? totalCount : listCount;
        return result;
    }

    private OrderBy getOrderBy(QueryBase args) {
        return QueryBuilder.ascDescOrder(OrderBy.fromProperty(Homework_Table.updatedAt),
                args.order == SortOrder.Asc);
    }

    private void saveToLocal(QueryResult<Homework> result) {
        if (QueryResult.isValidQueryResult(result) && result.isFetchFromCloud()) {
            clearTable();
            saveToLocal(result.list);
        }
    }

    private void clearTable() {
        try {
            StoreUtils.clearTable(Homework.class);
        } catch (Exception ignored) {
        }
    }

    private void saveToLocal(List<Homework> cloudList) {
        if (CollectionUtils.isNullOrEmpty(cloudList)) {
            return;
        }
        try {
            StoreUtils.saveToLocal(ContentDatabase.class, cloudList);
        } catch (Exception e) {
            Log.e(TAG, "saveToLocal", e);
        }
    }
}
