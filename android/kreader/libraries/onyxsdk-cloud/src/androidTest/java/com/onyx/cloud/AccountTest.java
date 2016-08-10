package com.onyx.cloud;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.cloud.model.OnyxAccount;
import com.onyx.cloud.service.OnyxAccountService;
import com.onyx.cloud.service.ServiceFactory;

import java.util.UUID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AccountTest extends ApplicationTestCase<Application> {

    public AccountTest() {
        super(Application.class);
    }

    public void testSignUp() {
        final OnyxAccountService service = ServiceFactory.getAccountService();
        final OnyxAccount account = new OnyxAccount(UUID.randomUUID().toString(), UUID.randomUUID().toString() + "@" + "onyx-international.com", UUID.randomUUID().toString());
        assertTrue(service.signup(account));
    }

}