package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.personal.cloud.api.GetSyncLoginInfoService;
import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.personal.common.CloudApiContext;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        GetSyncLoginInfoService service = init(CloudApiContext.getJdBaseUrl());
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

    private Call<SyncLoginInfoBean> getCall(GetSyncLoginInfoService service) {
        return service.getSyncLoginInfo(CloudApiContext.NewBookDetail.SYNC_LOGIN_INFO,
                requestBean.getAppBaseInfo().getRequestParamsMap());
    }

    private GetSyncLoginInfoService init(String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(CloudApiContext.getClient())
                .build();
        return retrofit.create(GetSyncLoginInfoService.class);
    }

}
