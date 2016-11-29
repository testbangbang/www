package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Link;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.StringUtils;

import retrofit2.Response;


/**
 * Created by suicheng on 2016/10/17.
 */

public class ProductRequest extends BaseCloudRequest {

    private String guid;
    private Product product;
    private Link link;

    public ProductRequest(String guid) {
        this.guid = guid;
    }

    public Product getProduct() {
        return product;
    }

    public Link getDownloadLink() {
        return link;
    }

    private void parseLink(Product product, String cloudStorage) {
        if (product != null) {
            link = product.getFirstDownloadLink();
        }
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(guid)) {
            return;
        }
        Response<Product> response = executeCall(ServiceFactory.getBookStoreService(parent.getCloudConf().getApiBase()).book(guid));
        if (response.isSuccessful()) {
            product = response.body();
            parseLink(product, parent.getCloudConf().getCloudStorage());
        }
    }
}
