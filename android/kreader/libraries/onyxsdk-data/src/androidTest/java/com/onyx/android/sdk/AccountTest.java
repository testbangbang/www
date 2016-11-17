package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Captcha;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.request.cloud.AccountSignUpRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.v1.OnyxAccountService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.File;
import java.util.List;
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

    private static String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");

    public static int getNum(int start, int end) {
        return (int) (Math.random() * (end - start + 1) + start);
    }

    public static String getRandomTel() {
        int index = getNum(0, telFirst.length - 1);
        String first = telFirst[index];
        String second = String.valueOf(getNum(1, 888) + 10000).substring(1);
        String third = String.valueOf(getNum(1, 9100) + 10000).substring(1);
        return first + second + third;
    }

    public OnyxAccount testSignUpAndSignIn() throws Exception {
        OnyxAccount account = AccountUtils.getCurrentAccount();
        account.nickName = UUID.randomUUID().toString().substring(0, 5);
        account.mobile = getRandomTel();

        Call<OnyxAccount> object = getService().signup(account);
        Response<OnyxAccount> response = object.execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        OnyxAccount resultAccount = response.body();
        assertNotNull(resultAccount);
        assertNotNull(resultAccount.sessionToken);
        account.sessionToken = resultAccount.sessionToken;

        object = getService().signout(account.sessionToken);
        response = object.execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());

        account = AccountUtils.getCurrentAccount();
        object = getService().signin(account);
        response = object.execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        resultAccount = response.body();
        assertNotNull(resultAccount);
        assertNotNull(resultAccount.sessionToken);
        return resultAccount;
    }

    public void testGetAndUpdate() throws Exception {
        OnyxAccount resultAccount = testSignUpAndSignIn();
        Response<OnyxAccount> response;

        //test getMetadataById accountInfo
        OnyxAccount accountInfo;
        response = getService().getAccountInfo(resultAccount.sessionToken).execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        accountInfo = response.body();
        assertNotNull(accountInfo);
        assertEquals(accountInfo.nickName, resultAccount.nickName);
        assertEquals(accountInfo.mobile, resultAccount.mobile);

        // test update Account
        OnyxAccount updateAccount;
        accountInfo.sessionToken = resultAccount.sessionToken;
        accountInfo.firstName = UUID.randomUUID().toString().substring(0, 5);
        accountInfo.mobile = null;
        response = getService().updateAccountInfo(accountInfo, accountInfo.sessionToken).execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        updateAccount = response.body();
        assertNotNull(updateAccount);
        assertEquals(updateAccount.firstName, accountInfo.firstName);

        // test upload avatar
        if (StringUtils.isNullOrEmpty(updateAccount.avatarUrl)) {
            File avatarFile = AccountUtils.getAvatarFile();
            assertTrue(avatarFile.exists());
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), avatarFile);
            MultipartBody.Part partBody = MultipartBody.Part.createFormData(Constant.AVATAR_TAG, avatarFile.getName(), requestFile);
            OnyxAccount uploadAvatarAccount;
            response = service.uploadAvatar(partBody, resultAccount.sessionToken).execute();
            assertNotNull(response);
            assertTrue(response.isSuccessful());
            assertNotNull(response.body());
            uploadAvatarAccount = response.body();
            assertNotNull(uploadAvatarAccount);
            assertNotNull(uploadAvatarAccount.avatarUrl);
        }
    }

    public void testCaptcha() throws Exception {
        Call<Captcha> call = getService().generateCaptcha();
        Response<Captcha> response = call.execute();
        assertNotNull(response);
        assertNotNull(response.body());
        assertNotNull(response.body().url);
    }

    public static OnyxAccount testSignUpRequest() throws Exception {
        OnyxAccount account = AccountUtils.generateRandomAccount();
        final CloudManager cloudManager = new CloudManager();
        AccountSignUpRequest accountSignUpRequest = new AccountSignUpRequest(account);
        accountSignUpRequest.execute(cloudManager);
        final OnyxAccount result = accountSignUpRequest.getAccountSignUp();
        assertNotNull(result);
        assertNotNull(result.sessionToken);
        account.sessionToken = result.sessionToken;
        return account;
    }

    public void testBindDevice() throws Exception {
        OnyxAccount account = testSignUpRequest();

        //remove first avoid duplicate device
        Response<ResponseBody> removeResponse = getService().removeBoundDevice(Device.updateCurrentDeviceInfo(getContext()).deviceUniqueId, account.sessionToken).execute();
        assertNotNull(removeResponse);

        // test addMetadataToLibrary device
        Response<Device> response = getService().addDevice(Device.updateCurrentDeviceInfo(getContext()), account.sessionToken).execute();
        assertNotNull(response);
        assertNotNull(response.body());
        Device device = response.body();
        assertEquals(device.deviceUniqueId, Device.updateCurrentDeviceInfo(getContext()).deviceUniqueId);
        assertNotNull(device.accountId);

        //test get bound Device
        response = getService().getBoundDevice(device.deviceUniqueId, account.sessionToken).execute();
        assertNotNull(response);
        assertNotNull(response.body());
        Device specDevice = response.body();
        assertEquals(device.accountId, specDevice.accountId);
        assertEquals(device.model, specDevice.model);

        //test get bound device list
        Response<List<Device>> listResponse = getService().getBoundDeviceList(account.sessionToken).execute();
        assertNotNull(listResponse);
        assertNotNull(listResponse.body());
        List<Device> deviceList = listResponse.body();
        assertTrue(deviceList.size() > 0);
        assertEquals(deviceList.get(0).accountId, specDevice.accountId);

        //test unbound device
        removeResponse = getService().removeBoundDevice(specDevice.deviceUniqueId, account.sessionToken).execute();
        assertNotNull(removeResponse);
        assertTrue(removeResponse.isSuccessful());
        assertNotNull(removeResponse.body());
    }
}