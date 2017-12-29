package com.onyx.jdread.setting.request;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.v1.OnyxOTAService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/12/26.
 */

public class RxCheckApkUpdateRequest extends RxBaseCloudRequest {
    private List<ApplicationUpdate> list;
    private List<ApplicationUpdate> queryList;
    private ProductQuery query;
    private CloudManager cloudManager;

    public RxCheckApkUpdateRequest(CloudManager cloudManager, List<ApplicationUpdate> queryList) {
        this.queryList = queryList;
        this.cloudManager = cloudManager;
    }

    public RxCheckApkUpdateRequest(CloudManager cloudManager, ProductQuery query) {
        this.query = query;
        this.cloudManager = cloudManager;
    }

    public List<ApplicationUpdate> getApplicationUpdateList() {
        return this.list;
    }

    public ApplicationUpdate getApplicationUpdate() {
        return CollectionUtils.isNullOrEmpty(this.list) ? null : (ApplicationUpdate) this.list.get(0);
    }

    @Override
    public Object call() throws Exception {
        Call<List<ApplicationUpdate>> call = getCall(ServiceFactory.getOTAService(cloudManager.getCloudConf().getApiBase()));
        Response<List<ApplicationUpdate>> response = call.execute();
        if (response.isSuccessful()) {
            list = response.body();
        }
        return this;
    }

    private Call<List<ApplicationUpdate>> getCall(OnyxOTAService service) {
        return CollectionUtils.isNullOrEmpty(this.queryList) ? service.getAllUpdateAppInfoList(JSON.toJSONString(this.query)) : service.getUpdateAppInfoList(JSON.toJSONString(this.queryList));
    }
}
