package com.onyx.cloud;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.cloud.model.OnyxAccount;
import com.onyx.cloud.service.OnyxAccountService;
import com.onyx.cloud.service.ServiceFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.util.UUID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AccountTest extends ApplicationTestCase<Application> {

    public AccountTest() {
        super(Application.class);
    }

    public void testSignUp() throws Exception {
        final OnyxAccountService service = ServiceFactory.getAccountService();
        final OnyxAccount account = new OnyxAccount(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                TestUtils.randomEmail());
        Call<OnyxAccount> object = service.signup(account);
        Response<OnyxAccount> response = object.execute();
        assertNotNull(response);
        final OnyxAccount resultAccount = response.body();
        assertNotNull(resultAccount);

    }

}