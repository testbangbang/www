package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.v1.OnyxOTAService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/26.
 */
public class ApplicationUpdateRequest extends BaseCloudRequest {
    private ProductQuery query;
    private List<ApplicationUpdate> queryList;
    private List<ApplicationUpdate> list;

    /**
     * use this method as get all update app list based on query
     */
    public ApplicationUpdateRequest(ProductQuery query) {
        this.query = query;
    }

    /**
     * use this method as get specification update app list
     */
    public ApplicationUpdateRequest(List<ApplicationUpdate> queryList) {
        this.queryList = queryList;
    }

    public List<ApplicationUpdate> getApplicationUpdateList() {
        return this.list;
    }

    public ApplicationUpdate getApplicationUpdate() {
        return CollectionUtils.isNullOrEmpty(list) ? null : list.get(0);
    }

    public List<ApplicationUpdate> getUpdateList() {
        return list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<ApplicationUpdate>> response = executeCall(call(ServiceFactory.getOTAService(parent.getCloudConf().getApiBase())));
        if (response.isSuccessful()) {
            list = response.body();
        }
    }

    private Call<List<ApplicationUpdate>> call(OnyxOTAService service) {
        if (CollectionUtils.isNullOrEmpty(queryList)) {
            return service.getAllUpdateAppInfoList(JSON.toJSONString(query));
        }
        return service.getUpdateAppInfoList(JSON.toJSONString(queryList));
    }
}
