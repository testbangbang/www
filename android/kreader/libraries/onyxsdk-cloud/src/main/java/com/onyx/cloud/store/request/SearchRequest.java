package com.onyx.cloud.store.request;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductResult;
import com.onyx.cloud.model.ProductSearch;
import com.onyx.cloud.service.OnyxBookStoreService;
import com.onyx.cloud.service.ServiceFactory;
import com.onyx.cloud.utils.CloudUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/30/15.
 */
public class SearchRequest extends BaseCloudRequest {

    private ProductSearch searchCriteria;
    private ProductResult<Product> searchResult;
    private GAdapter adapter;

    public SearchRequest(final ProductSearch criteria) {
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
        Call<ProductResult<Product>> call = ServiceFactory.getSpecService(OnyxBookStoreService.class, parent.getCloudConf().getApiBase() + "/")
                .bookSearch(param);
        Response<ProductResult<Product>> response = call.execute();
        if (response.isSuccessful()) {
            searchResult = response.body();
        }
    }
}
