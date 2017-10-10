package com.onyx.android.sdk.data.request.cloud.v2;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.converter.QueryArgsFilter;
import com.onyx.android.sdk.data.db.PushDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.data.model.v2.PushNotification;
import com.onyx.android.sdk.data.model.v2.PushNotification_Table;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/8/19.
 */
public class PushNotificationLoadRequest extends BaseCloudRequest {

    private List<String> idList;
    private QueryResult<PushNotification> result = new QueryResult<>();

    private boolean useMap;
    private Map<String, PushNotification> idMap = new HashMap<>();
    private QueryArgs queryArgs = new QueryArgs();

    public PushNotificationLoadRequest(List<String> idList, boolean useMap) {
        this.idList = idList;
        this.useMap = useMap;
        queryArgs.fetchPolicy = FetchPolicy.DB_ONLY;
    }

    public PushNotificationLoadRequest(QueryArgs args) {
        this.queryArgs = args;
    }

    public PushNotificationLoadRequest setQueryType(String type) {
        queryArgs.query = type;
        return this;
    }

    public PushNotificationLoadRequest setUseMap(boolean useMap) {
        this.useMap = useMap;
        return this;
    }

    public List<PushNotification> getNotificationList() {
        return result.getEnsureList();
    }

    public Map<String, PushNotification> getNotificationMap() {
        return idMap;
    }

    QueryResult<PushNotification> getQueryResult() {
        return result;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        result = fetchData(parent);
        obtainNotificationProductIdMap(result.list);
    }

    private QueryResult<PushNotification> fetchData(CloudManager parent) {
        QueryResult<PushNotification> result = new QueryResult<>();
        if (FetchPolicy.isCloudPartPolicy(queryArgs.fetchPolicy)) {
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
        } else {
            result = fetchFromLocal();
        }
        return result;
    }

    private QueryResult<PushNotification> fetchFromLocal() {
        Where<PushNotification> where = new Select().from(PushNotification.class).where();
        ConditionGroup condition = getIdsCondition();
        if (condition != null) {
            where.and(condition);
        }
        int limit = Math.max(CollectionUtils.getSize(idList), queryArgs.limit);
        int offset = CollectionUtils.isNullOrEmpty(idList) ? queryArgs.offset : 0;
        PushNotification query = JSONObjectParseUtils.parseObject(queryArgs.query, PushNotification.class);
        if (query != null) {
            where.and(PushNotification_Table.type.eq(query.type));
        }
        List<PushNotification> notificationList = where
                .orderBy(getOrderBy(queryArgs))
                .limit(limit).offset(offset)
                .queryList();
        QueryResult<PushNotification> result = new QueryResult<>();
        result.list = notificationList;
        result.count = CollectionUtils.getSize(notificationList);
        result.fetchSource = Metadata.FetchSource.LOCAL;
        return result;
    }

    private QueryResult<PushNotification> fetchFromCloud(CloudManager parent) throws Exception {
        String param = JSON.toJSONString(queryArgs, new QueryArgsFilter() {
            @Override
            protected String getString(SortBy sortBy) {
                return getName(PushNotification_Table.createdAt);
            }
        });
        Response<QueryResult<PushNotification>> response = executeCall(ServiceFactory.getContentService(parent.getCloudConf().getApiBase())
                .getPushNotificationResult(param));
        if (response.isSuccessful()) {
            QueryResult<PushNotification> result = response.body();
            result.fetchSource = Metadata.FetchSource.CLOUD;
            return result;
        }
        return new QueryResult<>();
    }

    private void obtainNotificationProductIdMap(List<PushNotification> list) {
        if (useMap && !CollectionUtils.isNullOrEmpty(list)) {
            for (PushNotification item : list) {
                idMap.put(item.productId, item);
            }
        }
    }

    private void saveToLocal(QueryResult<PushNotification> result) {
        if (QueryResult.isValidQueryResult(result) && result.isFetchFromCloud()) {
            syncCloudDataToLocal(result.list);
        }
    }

    private void syncCloudDataToLocal(List<PushNotification> cloudList) {
        syncCloudDataFromLocal(cloudList);
        clearPushNotification();
        saveToLocal(cloudList);
    }

    private void syncCloudDataFromLocal(List<PushNotification> cloudList) {
        List<PushNotification> localList = fetchFromLocal().list;
        if (!CollectionUtils.isNullOrEmpty(localList)) {
            for (PushNotification cloud : cloudList) {
                for (PushNotification local : localList) {
                    if (objectEqual(local._id, cloud._id)) {
                        cloud.isReaded = local.isReaded;
                        localList.remove(local);
                        break;
                    }
                }
                if (CollectionUtils.isNullOrEmpty(localList)) {
                    break;
                }
            }
        }
    }

    private void saveToLocal(List<PushNotification> cloudList) {
        if (CollectionUtils.isNullOrEmpty(cloudList)) {
            return;
        }
        StoreUtils.saveToLocal(PushDatabase.class, cloudList, PushNotification.class, false);
    }

    private void clearPushNotification() {
        clearPushNotification(PushNotification_Table.type.eq(getQueryType()));
    }

    private void clearPushNotification(Condition... conditions) {
        Where<PushNotification> where = new Delete().from(PushNotification.class).where();
        if (conditions != null && conditions.length > 0) {
            for (Condition condition : conditions) {
                where.and(condition);
            }
        }
        where.execute();
    }

    private boolean objectEqual(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    private String getQueryType() {
        PushNotification query = JSONObjectParseUtils.parseObject(queryArgs.query, PushNotification.class);
        if (query == null) {
            return null;
        }
        return query.type;
    }

    private OrderBy getOrderBy(QueryArgs args) {
        return QueryBuilder.ascDescOrder(OrderBy.fromProperty(PushNotification_Table.createdAt),
                args.order == SortOrder.Asc);
    }

    private ConditionGroup getIdsCondition() {
        return DataManagerHelper.getPropertyOrCondition(idList, PushNotification_Table.productId);
    }
}
