package com.onyx.cloud;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.cloud.model.OnyxAccount;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductResult;
import com.onyx.cloud.service.OnyxAccountService;
import com.onyx.cloud.service.OnyxBookStoreService;
import com.onyx.cloud.service.ServiceFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;
import java.util.UUID;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class BookStoreTest extends ApplicationTestCase<Application> {

    static OnyxBookStoreService service = ServiceFactory.getBookStoreService();
    static List<Product> productList;

    public BookStoreTest() {
        super(Application.class);
    }

    public void testBookList()  throws Exception {
        Call<ProductResult<Product>> object = service.bookList();
        Response<ProductResult<Product>> response = object.execute();
        assertNotNull(response.body());
        assertTrue(response.body().count > 0);
        productList = response.body().list;
        assertNotNull(productList);
    }

    public void testSingleBook()  throws Exception {
        final String uniqueId = productList.get(0).getIdString();
        Call<Product> object = service.book(uniqueId);
        Response<Product> response = object.execute();
        assertTrue(response.body().getIdString().equals(uniqueId));
    }

}
