package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.BaseQuery;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.ProductShared;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2017/2/20.
 */
public class ProductSharedListRequest extends BaseCloudRequest {

    private BaseQuery query;
    private List<ProductShared> productSharedList;

    public ProductSharedListRequest(BaseQuery query) {
        this.query = query;
    }

    public List<ProductShared> getProductSharedList() {
        return productSharedList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<ProductResult<ProductShared>> response = executeCall(ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .productSharedList(JSON.toJSONString(query)));
        if (response.isSuccessful()) {
            ProductResult<ProductShared> productResult = response.body();
            if (productResult != null) {
                productSharedList = productResult.list;
            }
        }
    }
}
