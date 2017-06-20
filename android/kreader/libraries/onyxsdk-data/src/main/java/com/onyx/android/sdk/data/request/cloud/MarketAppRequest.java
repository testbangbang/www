package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.AppProduct;
import com.onyx.android.sdk.data.model.Link;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/3/1.
 */
public class MarketAppRequest extends BaseCloudRequest {

    private String guid;
    private AppProduct appProduct;
    private Link link;

    public MarketAppRequest(String guid) {
        this.guid = guid;
    }

    public AppProduct getAppProduct() {
        return appProduct;
    }

    public Link getDownloadLink() {
        return link;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<AppProduct> response = executeCall(ServiceFactory.getOTAService(parent.getCloudConf().getApiBase())
                .getMarketApp(guid));
        if (response.isSuccessful()) {
            appProduct = response.body();
            if (appProduct != null) {
                link = appProduct.getFirstDownloadLink();
            }
        }
    }
}
