package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.PushOssProduct;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/1/21.
 */
public class PushSavingProductRequest extends BaseCloudRequest {

    private PushOssProduct pushOssProduct;
    private Product resultProduct;

    public PushSavingProductRequest(PushOssProduct pushOssProduct) {
        this.pushOssProduct = pushOssProduct;
    }

    public Product getResultProduct() {
        return resultProduct;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<Product> response = executeCall(ServiceFactory.getPushService(parent.getCloudConf().getApiBase())
                .pushSavingBook(pushOssProduct, getAccountSessionToken()));
        if (response.isSuccessful()) {
            resultProduct = response.body();
        }
    }
}
