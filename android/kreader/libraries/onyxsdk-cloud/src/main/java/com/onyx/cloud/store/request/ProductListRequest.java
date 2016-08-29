package com.onyx.cloud.store.request;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductQuery;
import com.onyx.cloud.model.ProductResult;
import com.onyx.cloud.service.v1.ServiceFactory;
import com.onyx.cloud.utils.CloudUtils;
import com.onyx.cloud.utils.StoreUtils;

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
        Response<ProductResult<Product>> response = ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .bookList(param).execute();
        CloudUtils.dumpResponseMessage(TAG, response, true);
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
