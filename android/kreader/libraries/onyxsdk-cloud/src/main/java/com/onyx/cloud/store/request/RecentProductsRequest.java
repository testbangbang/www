package com.onyx.cloud.store.request;

import com.onyx.cloud.CloudManager;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductResult;
import com.onyx.cloud.service.OnyxBookStoreService;
import com.onyx.cloud.service.ServiceFactory;
import com.onyx.cloud.utils.StoreUtils;

import retrofit2.Response;

/**
 * Created by zhuzeng on 12/3/15.
 */
public class RecentProductsRequest extends BaseCloudRequest {

    private ProductResult<Product> productResult;

    public RecentProductsRequest() {
    }

    public final ProductResult<Product> getProductResult() {
        return productResult;
    }

    public void execute(final CloudManager parent) throws Exception {
        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        } else {
            fetchFromLocal(parent);
        }
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        OnyxBookStoreService service = ServiceFactory.getSpecService(OnyxBookStoreService.class, parent.getCloudConf().getApiBase() + "/");
        Response<ProductResult<Product>> response = service.bookRecentList().execute();
        if (response.isSuccessful()) {
            productResult = response.body();
        }
    }

    public void fetchFromLocal(final CloudManager parent) throws Exception {
        productResult = new ProductResult<>();
        productResult.list = StoreUtils.queryDataList(Product.class);
        productResult.count = productResult.list.size();
    }

    private void saveToLocal(final ProductResult<Product> result) {
        StoreUtils.saveToLocal(result, Product.class, true);
    }

}
