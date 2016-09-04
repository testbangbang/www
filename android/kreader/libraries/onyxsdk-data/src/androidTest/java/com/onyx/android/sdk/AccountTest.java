package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Captcha;
import com.onyx.android.sdk.data.request.cloud.SignInRequest;
import com.onyx.android.sdk.data.request.cloud.SignUpRequest;
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
    private static volatile OnyxAccount currentAccount;

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

    private OnyxAccount getCurrentAccount() {
        if (currentAccount == null) {
            currentAccount = new OnyxAccount(UUID.randomUUID().toString(),
                    TestUtils.randString(), TestUtils.randomEmail());
        }
        return currentAccount;
    }

    public void testSignUpAndSignIn() throws Exception {
        OnyxAccount account = getCurrentAccount();
        Call<ResponseBody> object = getService().signup(account);
        Response<ResponseBody> response = object.execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        OnyxAccount resultAccount = JSONObjectParseUtils.parseOnyxAccount(response.body().string());
        assertNotNull(resultAccount);
        assertNotNull(resultAccount.sessionToken);
        account = resultAccount;

        object = getService().signout(account.sessionToken);
        response = object.execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());

        account = getCurrentAccount();
        object = getService().signin(account);
        response = object.execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        resultAccount = JSONObjectParseUtils.parseOnyxAccount(response.body().string());
        assertNotNull(resultAccount);
        assertNotNull(resultAccount.sessionToken);
    }

    public void testCaptcha() throws Exception {
        Call<Captcha> call = getService().generateCaptcha();
        Response<Captcha> response = call.execute();
        assertNotNull(response);
        assertNotNull(response.body());
        assertNotNull(response.body().url);
    }

    public void testSignUpRequest() throws Exception {
        OnyxAccount account = getCurrentAccount();
        final CloudManager cloudManager = new CloudManager();
        SignUpRequest signUpRequest = new SignUpRequest(account);
        signUpRequest.execute(cloudManager);
        final OnyxAccount result = signUpRequest.getAccountSignUp();
        assertNotNull(result);
        assertNotNull(result.sessionToken);
    }


}