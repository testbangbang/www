package com.onyx.jdread.shop.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.main.common.CommonUtils;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.cache.EnhancedCall;
import com.onyx.jdread.shop.cloud.entity.GetChapterGroupInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BaseResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetChaptersContentResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.io.File;
import java.util.List;

import retrofit2.Call;

/**
 * Created by jackdeng on 2018/3/9.
 */

public class RxRequestGetChaptersContent extends RxBaseCloudRequest {
    private GetChapterGroupInfoRequestBean requestBean;
    private GetChaptersContentResultBean resultBean;

    public GetChaptersContentResultBean getResultBean() {
        return resultBean;
    }

    public void setRequestBean(GetChapterGroupInfoRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public Object call() throws Exception {
        executeCloudRequest();
        return this;
    }

    private void executeCloudRequest() {
        ReadContentService getCommonService;
        if (requestBean.withCookie) {
            getCommonService = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl());
        } else {
            getCommonService = CloudApiContext.getServiceNoCookie(CloudApiContext.getJDBooxBaseUrl());
        }
        Call<GetChaptersContentResultBean> call = getCall(getCommonService);
        resultBean = done(call);
        checkResult();
    }

    private void checkResult() {
        if (resultBean != null) {
            if (BaseResultBean.checkSuccess(resultBean)) {
                String time = requestBean.getBaseInfo().getTime();
                String pin = ClientUtils.getWJLoginHelper().getPin();
                List<GetChaptersContentResultBean.DataBean> data = resultBean.data;
                if (data != null) {
                    for (int i = 0; i < data.size(); i++) {
                        GetChaptersContentResultBean.DataBean dataBean = data.get(i);
                        dataBean.time = time;
                        dataBean.pin = pin;
                        String path = getPath();
                        if (!FileUtils.fileExist(path)) {
                            FileUtils.mkdirs(path);
                        }
                        File file = new File(path, dataBean.id);
                        if (!FileUtils.fileExist(path)) {
                            FileUtils.saveContentToFile(JSONObjectParseUtils.toJson(dataBean), file);
                        }
                    }
                }
            } else {
                ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
            }
        }
    }

    private String getPath() {
        return CommonUtils.getJDNetBooksPath() + requestBean.bookId + "_" + requestBean.bookName;
    }

    private GetChaptersContentResultBean done(Call<GetChaptersContentResultBean> call) {
        EnhancedCall<GetChaptersContentResultBean> enhancedCall = new EnhancedCall<>(call);
        return enhancedCall.execute(call, GetChaptersContentResultBean.class);
    }

    private Call<GetChaptersContentResultBean> getCall(ReadContentService getCommonService) {
        return getCommonService.getChaptersContent(requestBean.bookId, requestBean.getBaseInfo().getRequestParamsMap());
    }
}
