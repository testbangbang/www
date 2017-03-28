package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.AppProduct;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/3/2.
 */
public class MarketAppSearchRequest extends BaseCloudRequest {

    private ProductQuery query;
    private List<AppProduct> productList;

    public MarketAppSearchRequest(ProductQuery query) {
        this.query = query;
    }

    public List<AppProduct> getProductList() {
        return productList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ProductResult<AppProduct>> response = executeCall(ServiceFactory.getOTAService(
                parent.getCloudConf().getApiBase()).getMarketAppSearch(JSON.toJSONString(query)));
        if (response.isSuccessful()) {
            productList = response.body().list;
        }
    }
}
