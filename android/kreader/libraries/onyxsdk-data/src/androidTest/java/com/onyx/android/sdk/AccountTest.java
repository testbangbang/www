package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Captcha;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.android.sdk.data.model.JsonResponse;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.v1.OnyxAccountService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.util.UUID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class AccountTest extends ApplicationTestCase<Application> {

    private static OnyxAccountService service;

    public AccountTest() {
        super(Application.class);
    }

    private final OnyxAccountService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getAccountService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public void testSignUp() throws Exception {
        final OnyxAccount account = new OnyxAccount(UUID.randomUUID().toString(),
                TestUtils.randString(), TestUtils.randomEmail());
        Call<ResponseBody> object = getService().signup(account);
        Response<ResponseBody> response = object.execute();
        assertNotNull(response);
        if (response.isSuccessful()) {
            assertNotNull(response.body());
            final OnyxAccount resultAccount = JSONObjectParseUtils.parseOnyxAccount(response.body().string());
            assertNotNull(resultAccount);
        } else {
            JsonResponse jsonResponse = JSON.parseObject(response.errorBody().string(), JsonResponse.class);
            assertNotNull(jsonResponse);

        }
    }

    public void testCaptcha() throws Exception {
        Call<Captcha> call = getService().getCaptcha();
        Response<Captcha> response = call.execute();
        assertNotNull(response);
        assertNotNull(response.body());
        assertNotNull(response.body().url);
    }
}