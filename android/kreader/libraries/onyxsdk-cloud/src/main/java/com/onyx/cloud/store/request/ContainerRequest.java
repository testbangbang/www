package com.onyx.cloud.store.request;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.ProductContainer;
import com.onyx.cloud.model.ProductQuery;
import com.onyx.cloud.model.ProductResult;
import com.onyx.cloud.service.OnyxBookStoreService;
import com.onyx.cloud.service.ServiceFactory;
import com.onyx.cloud.utils.CloudUtils;
import com.onyx.cloud.utils.StoreUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import retrofit2.Response;

/**
 * Created by zhuzeng on 11/21/15. Return all category currently supported.
 */
public class ContainerRequest extends BaseCloudRequest {
    private ProductQuery productQuery;
    private ProductResult<ProductContainer> productResult;
    private GAdapter adapter;

    public ContainerRequest(final ProductQuery query) {
        productQuery = query;
    }

    public final ProductResult<ProductContainer> getProductResult() {
        return productResult;
    }

    public final GAdapter getAdapter() {
        return adapter;
    }

    public void execute(final CloudManager parent) throws Exception {
        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        } else {
            fetchFromLocalCache(parent);
        }
        adapter = CloudUtils.adapterFromContainerResult(productResult);
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        OnyxBookStoreService service = ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase());
        Response<ProductResult<ProductContainer>> response = service.bookContainer(JSON.toJSONString(productQuery)).execute();
        if (response.isSuccessful()) {
            productResult = response.body();
            if (isSaveToLocal()) {
                saveToLocal(productResult);
            }
        }
    }

    public void fetchFromLocalCache(final CloudManager parent) throws Exception {
        productResult = new ProductResult<>();
        productResult.list = SQLite.select().from(ProductContainer.class).queryList();
        productResult.count = productResult.list.size();
    }

    private void saveToLocal(final ProductResult<ProductContainer> result) {
        StoreUtils.saveToLocal(result, ProductContainer.class, true);
    }
}
