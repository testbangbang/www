package com.onyx.android.dr.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.ArticleInfoBean;
import com.onyx.android.sdk.data.model.v2.GetArticlePushBean;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by zhouzhiming on 2017/10/10.
 */
public class RequestGetArticlePush extends AutoNetWorkConnectionBaseCloudRequest {
    private final String param;
    private final String id;
    private List<ArticleInfoBean> dataList = new ArrayList<>();

    public RequestGetArticlePush(String param, String id) {
        this.param = param;
        this.id = id;
    }

    public List<ArticleInfoBean> getGroup() {
        return dataList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        getMyGroup(parent);
    }

    private void getMyGroup(CloudManager parent) {
        try {
            Response<GetArticlePushBean> response = executeCall(ServiceFactory.getContentService(
                    parent.getCloudConf().getApiBase()).getArticlePush(id, param));
            if (response != null) {
                dataList = response.body().list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
