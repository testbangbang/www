package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxGetAccountInfoRequest;
import com.onyx.android.sdk.data.rxrequest.data.cloud.RxSignUpAccountRequest;
import com.onyx.android.sdk.rx.RxCallback;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * Created by jackdeng on 2017/11/7.
 */

public class RxAccountCloudRequestTest extends ApplicationTestCase<Application> {
    static OnyxAccount currentAccount;
    private OnyxAccount result;

    public RxAccountCloudRequestTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
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

    public static OnyxAccount getCurrentAccount() throws Exception {
        if (currentAccount == null || currentAccount.sessionToken == null) {
            currentAccount = AccountTest.testSignUpRequest();
        }
        return currentAccount;
    }

    public void testSignUpAccount() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final OnyxAccount account = AccountUtils.getCurrentAccount();
        account.nickName = UUID.randomUUID().toString().substring(0, 5);
        account.mobile = getRandomTel();
        RxSignUpAccountRequest request = new RxSignUpAccountRequest(account);
        request.execute(new RxCallback<RxSignUpAccountRequest>() {
            @Override
            public void onNext(RxSignUpAccountRequest request) {
                result = request.getResult();
                assertNotNull(result);
                assertNotNull(result.sessionToken);
                account.sessionToken = result.sessionToken;
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }

    public void testSignInAccount() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        testSignUpAccount();
        RxGetAccountInfoRequest request = new RxGetAccountInfoRequest(result.sessionToken);
        request.execute(new RxCallback<RxGetAccountInfoRequest>() {
            @Override
            public void onNext(RxGetAccountInfoRequest request) {
                OnyxAccount result = request.getResult();
                assertNotNull(result);
                assertNotNull(result.sessionToken);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
    }
}