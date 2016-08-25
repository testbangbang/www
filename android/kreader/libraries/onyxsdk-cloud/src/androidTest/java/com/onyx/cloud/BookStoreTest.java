package com.onyx.cloud;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductContainer;
import com.onyx.cloud.model.ProductQuery;
import com.onyx.cloud.model.ProductResult;
import com.onyx.cloud.model.ProductSearch;
import com.onyx.cloud.service.OnyxBookStoreService;
import com.onyx.cloud.service.ServiceFactory;

import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class BookStoreTest extends ApplicationTestCase<Application> {

    static OnyxBookStoreService service = ServiceFactory.getBookStoreService(ServiceFactory.API_V1_BASE);
    static List<Product> productList;

    public BookStoreTest() {
        super(Application.class);
    }

    public void testBookList() throws Exception {
        Call<ProductResult<Product>> object = service.bookList(JSON.toJSONString(new ProductQuery()));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        productList = response.body().list;
        assertNotNull(productList);
    }

    public void testSingleBook() throws Exception {
        final String uniqueId = productList.get(0).getIdString();
        Call<Product> object = service.book(uniqueId);
        Response<Product> response = object.execute();
        assertTrue(response.body().getIdString().equals(uniqueId));
    }

    public void testBookListWithProductQuery() throws Exception {
        ProductQuery productQuery = new ProductQuery();
        productQuery.count = 2;
        productQuery.useCategory("100");
        Call<ProductResult<Product>> object = service.bookList(JSON.toJSONString(productQuery));
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
        Call<ProductResult<Product>> object = service.bookSearch(JSON.toJSONString(productSearch));
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        assertNotNull(response.body().list);
        assertTrue(response.body().list.size() <= 5);
    }

    public void testContainer() throws Exception {
        ProductQuery productQuery = new ProductQuery();
        Call<ProductResult<ProductContainer>> object = service.bookContainer(JSON.toJSONString(productQuery));
        Response<ProductResult<ProductContainer>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        List<ProductContainer> list = response.body().list;
        assertNotNull(list);
    }
}
