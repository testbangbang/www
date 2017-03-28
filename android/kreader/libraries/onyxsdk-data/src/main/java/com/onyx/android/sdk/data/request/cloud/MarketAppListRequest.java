package com.onyx.android.sdk.data.request.cloud;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.AppProduct;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/3/1.
 */
public class MarketAppListRequest extends BaseCloudRequest {

    private ProductQuery query;
    private List<AppProduct> productList;

    public MarketAppListRequest(ProductQuery query) {
        this.query = query;
    }

    public List<AppProduct> getProductList() {
        return productList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        } else {
            fetchFromLocalCache(parent);
        }
    }

    private void fetchFromLocalCache(final CloudManager parent) throws Exception {
        productList = StoreUtils.queryDataList(AppProduct.class);
    }

    private void fetchFromCloud(CloudManager parent) throws Exception {
        Response<ProductResult<AppProduct>> response = executeCall(ServiceFactory.getOTAService(
                parent.getCloudConf().getApiBase()).getMarketAppList(JSON.toJSONString(query)));
        if (response.isSuccessful()) {
            productList = response.body().list;
            StoreUtils.saveToLocal(productList, AppProduct.class, true);
        }
    }
}
