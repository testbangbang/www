package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.PushBroadcast;
import com.onyx.android.sdk.data.model.PushRecord;
import com.onyx.android.sdk.data.model.PushBroadcast_Table;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/11/2.
 */

public class PushBroadcastRequest extends BaseCloudRequest {
    private boolean cloudOnly;
    private int queryOffset = 0;
    private int limit = 8;
    private List<PushRecord> recordList = new ArrayList<>();
    private List<PushBroadcast> broadcastList = new ArrayList<>();

    public PushBroadcastRequest(boolean cloud, int offset) {
        cloudOnly = cloud;
        queryOffset = offset;
    }

    public List<PushBroadcast> getBroadcastList() {
        return broadcastList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (cloudOnly) {
            fetchFromCloud(parent);
            return;
        }
        fetchFromLocalCache(parent);
    }

    public void fetchFromLocalCache(final CloudManager parent) throws Exception {
        List<PushBroadcast> list = fetchFromLocalCache(false, queryOffset, limit);
        int remain = limit - list.size();
        if (remain > 0) {
            list.addAll(fetchFromLocalCache(true, queryOffset, remain));
        }
        broadcastList = list;
    }

    private List<PushBroadcast> fetchFromLocalCache(boolean isReadCondition, int offset, int limit) {
        return SQLite.select().from(PushBroadcast.class)
                .where(PushBroadcast_Table.isReaded.is(isReadCondition))
                .orderBy(OrderBy.fromProperty(PushBroadcast_Table.createdAt).descending())
                .offset(offset)
                .limit(limit)
                .queryList();
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        Response<ProductResult<PushRecord>> response = executeCall(ServiceFactory.getPushService(parent.getCloudConf().getApiBase())
                .pushBroadcastList(getAccountSessionToken()));
        if (response.isSuccessful()) {
            ProductResult<PushRecord> result = response.body();
            if (!StoreUtils.isEmpty(result)) {
                recordList = result.list;
                for (PushRecord pushRecord : result.list) {
                    PushBroadcast broadcast = pushRecord.parsePushBroadcast();
                    if (broadcast != null) {
                        broadcastList.add(broadcast);
                    }
                }
            }
        }
    }
}
