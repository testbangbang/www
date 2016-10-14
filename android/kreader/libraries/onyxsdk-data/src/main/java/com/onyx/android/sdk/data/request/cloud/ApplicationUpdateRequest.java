package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/26.
 */
public class ApplicationUpdateRequest extends BaseCloudRequest {

    private List<ApplicationUpdate> queryList;
    private List<ApplicationUpdate> list;

    public ApplicationUpdateRequest(List<ApplicationUpdate> queryList) {
        this.queryList = queryList;
    }

    public List<ApplicationUpdate> getApplicationUpdateList() {
        return this.list;
    }

    public ApplicationUpdate getApplicationUpdate() {
        return CollectionUtils.isNullOrEmpty(list) ? null : list.get(0);
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<List<ApplicationUpdate>> response = ServiceFactory.getOTAService(parent.getCloudConf().getApiBase())
                .getUpdateAppInfoList(JSON.toJSONString(queryList)).execute();
        if (response.isSuccessful()) {
            list = response.body();
        }
    }
}
