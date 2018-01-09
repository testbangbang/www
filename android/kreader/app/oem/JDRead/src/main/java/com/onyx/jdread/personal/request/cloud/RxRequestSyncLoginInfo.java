package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.CloudApiContext;
import com.onyx.jdread.main.servie.ReadContentService;
import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class RxRequestSyncLoginInfo extends RxBaseCloudRequest {
    private BaseRequestBean requestBean;
    private SyncLoginInfoBean syncLoginInfoBean;

    public void setRequestBean(BaseRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public SyncLoginInfoBean getSyncLoginInfoBean() {
        return syncLoginInfoBean;
    }

    @Override
    public Object call() throws Exception {
        ReadContentService service = CloudApiContext.getService(CloudApiContext.getJdBaseUrl());
        Call<SyncLoginInfoBean> call = getCall(service);
        Response<SyncLoginInfoBean> response = call.execute();
        if (response != null) {
            syncLoginInfoBean = response.body();
            checkQuestResult();
        }
        return this;
    }

    private void checkQuestResult() {
        if (syncLoginInfoBean != null && !StringUtils.isNullOrEmpty(syncLoginInfoBean.getCode())) {
            switch (syncLoginInfoBean.getCode()) {

            }
        }
    }

    private Call<SyncLoginInfoBean> getCall(ReadContentService service) {
        return service.getSyncLoginInfo(CloudApiContext.NewBookDetail.SYNC_LOGIN_INFO,
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }
}
