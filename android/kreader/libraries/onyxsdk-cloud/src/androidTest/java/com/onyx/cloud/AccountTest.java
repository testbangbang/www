package com.onyx.cloud;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.cloud.model.Captcha;
import com.onyx.cloud.model.OnyxAccount;
import com.onyx.cloud.service.OnyxAccountService;
import com.onyx.cloud.service.ServiceFactory;
import com.onyx.cloud.utils.JSONObjectParseUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.util.UUID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AccountTest extends ApplicationTestCase<Application> {

    static OnyxAccountService service = ServiceFactory.getAccountService(ServiceFactory.API_V1_BASE);

    public AccountTest() {
        super(Application.class);
    }

    public static String randomEmail() {
        String email = UUID.randomUUID().toString().replace("-", "") + "@" + "onyx-international.com";
        while (Character.isDigit(email.charAt(0))) {
            email = email.substring(1);
        }
        return email;
    }

    public void testSignUp() throws Exception {
        final OnyxAccount account = new OnyxAccount(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                randomEmail());
        Call<ResponseBody> object = service.signup(account);
        Response<ResponseBody> response = object.execute();
        assertNotNull(response);
        if (response.isSuccessful()) {
            assertNotNull(response.body());
            final OnyxAccount resultAccount = JSONObjectParseUtils.parseOnyxAccount(response.body().string());
            assertNotNull(resultAccount);
        } else {
            assertNotNull(response.errorBody());
        }
    }

    public void testCaptcha() throws Exception {
        Call<Captcha> call = service.getCaptcha();
        Response<Captcha> response = call.execute();
        assertNotNull(response);
        assertNotNull(response.body());
        assertNotNull(response.body().url);
    }
}