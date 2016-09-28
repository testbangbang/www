package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.model.*;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.v1.OnyxBookStoreService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class BookStoreTest extends ApplicationTestCase<Application> {

    static OnyxBookStoreService service;
    static List<Product> productList;

    public BookStoreTest() {
        super(Application.class);
    }

    private final OnyxBookStoreService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getBookStoreService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public void testBookList() throws Exception {
        Call<ProductResult<Product>> object = getService().bookList(JSON.toJSONString(new ProductQuery()));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        productList = response.body().list;
        assertNotNull(productList);
    }

    public void testSingleBook() throws Exception {
        final String uniqueId = productList.get(0).getIdString();
        Call<Product> object = getService().book(uniqueId);
        Response<Product> response = object.execute();
        assertTrue(response.body().getIdString().equals(uniqueId));
    }

    public void testBookListWithProductQuery() throws Exception {
        ProductQuery productQuery = new ProductQuery();
        productQuery.count = 2;
        productQuery.useCategory("100");
        Call<ProductResult<Product>> object = getService().bookList(JSON.toJSONString(productQuery));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        assertNotNull(response.body().list);
        assertTrue(response.body().list.size() <= 2);
    }

    public void testBookSearch() throws Exception {
        ProductSearch productSearch = new ProductSearch();
        productSearch.limit = 5;
        productSearch.pattern = "三";
        Call<ProductResult<Product>> object = getService().bookSearch(JSON.toJSONString(productSearch));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        assertNotNull(response.body().list);
        assertTrue(response.body().list.size() <= 5);
    }

    public void testContainer() throws Exception {
        Call<List<Category>> object = getService().bookContainerList();
        Response<List<Category>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().size() > 0);
        List<Category> list = response.body();
        assertNotNull(list);
    }
}
