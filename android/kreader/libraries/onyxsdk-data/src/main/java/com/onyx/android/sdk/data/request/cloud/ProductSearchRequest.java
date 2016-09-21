package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.ProductSearch;
import com.onyx.android.sdk.data.utils.CloudUtils;

import com.onyx.android.sdk.data.v1.ServiceFactory;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/30/15.
 */
public class ProductSearchRequest extends BaseCloudRequest {

    private ProductSearch searchCriteria;
    private ProductResult<Product> searchResult;
    private GAdapter adapter;

    public ProductSearchRequest(final ProductSearch criteria) {
        searchCriteria = criteria;
    }

    public final ProductResult<Product> getSearchResult() {
        return searchResult;
    }

    public final GAdapter getAdapter() {
        return adapter;
    }

    public void execute(final CloudManager parent) throws Exception {
        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        }
        adapter = CloudUtils.adapterFromProductResult(getContext(), searchResult, searchCriteria.limit, parent.getCloudConf());
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        String param = JSON.toJSONString(searchCriteria);
        Call<ProductResult<Product>> call = ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase())
                .bookSearch(param);
        Response<ProductResult<Product>> response = call.execute();
        if (response.isSuccessful()) {
            searchResult = response.body();
        }
    }
}
