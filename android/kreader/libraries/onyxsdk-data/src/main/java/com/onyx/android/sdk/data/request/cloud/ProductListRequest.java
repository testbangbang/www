package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductQuery;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;

import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/8/12.
 */
public class ProductListRequest extends BaseCloudRequest {
    static private final String TAG = ProductListRequest.class.getSimpleName();
    private ProductQuery productQuery;
    private ProductResult<Product> productResult;
    private GAdapter adapter;
    private volatile boolean clearCache;
    private volatile boolean cloudOnly;

    public ProductListRequest(final ProductQuery query, boolean clear, boolean cloud) {
        productQuery = query;
        clearCache = clear;
        cloudOnly = cloud;
    }

    public final ProductResult<Product> getProductResult() {
        return productResult;
    }

    public final GAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        } else if (!cloudOnly) {
            fetchFromLocalCache(parent);
        }
        adapter = CloudUtils.adapterFromProductResult(getContext(), productResult, productQuery.coverLimit, parent.getCloudConf());
    }

    public void fetchFromLocalCache(final CloudManager parent) throws Exception {
        productResult = new ProductResult<>();
        productResult.list = StoreUtils.queryDataList(Product.class);
        productResult.count = productResult.list.size();
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        String param = JSON.toJSONString(productQuery);
        Response<ProductResult<Product>> response = executeCall(ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .bookList(param));
        if (response.isSuccessful()) {
            productResult = response.body();
            if (isSaveToLocal()) {
                saveToLocal(productResult);
            }
        }
    }

    private void saveToLocal(final ProductResult<Product> result) {
        StoreUtils.saveToLocal(result, Product.class, clearCache);
    }
}
