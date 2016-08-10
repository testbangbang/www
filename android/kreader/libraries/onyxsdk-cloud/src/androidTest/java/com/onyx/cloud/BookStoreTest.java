package com.onyx.cloud;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.cloud.model.OnyxAccount;
import com.onyx.cloud.service.OnyxAccountService;
import com.onyx.cloud.service.OnyxBookStoreService;
import com.onyx.cloud.service.ServiceFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.util.UUID;

/**
 * Created by zhuzeng on 8/10/16.
 */
public class BookStoreTest extends ApplicationTestCase<Application> {

    public BookStoreTest() {
        super(Application.class);
    }

    public void testSignUp()  throws Exception {
        final OnyxBookStoreService service = ServiceFactory.getBookStoreService();
        Call<Object> object = service.bookList();
        Response<Object> response = object.execute();
        assertNotNull(response);
    }

}
