package com.onyx.jdread;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.personal.cloud.entity.GetOrderRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.personal.common.CloudApiContext;
import com.onyx.jdread.personal.request.cloud.RxGetOrderUrlRequest;
import com.onyx.jdread.personal.request.cloud.RxRequestSyncLoginInfo;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import jd.wjlogin_sdk.common.WJLoginHelper;
import jd.wjlogin_sdk.common.listener.OnLoginCallback;
import jd.wjlogin_sdk.model.FailResult;
import jd.wjlogin_sdk.model.JumpResult;
import jd.wjlogin_sdk.model.PicDataInfo;
import jd.wjlogin_sdk.util.MD5;

;

/**
 * Created by li on 2017/12/30.
 */

public class GetOrderUrlTest extends ApplicationTestCase<JDReadApplication> {
    private static final String TAG = GetOrderUrlTest.class.getSimpleName();

    public GetOrderUrlTest() {
        super(JDReadApplication.class);
    }

    public void testLogin() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final WJLoginHelper helper = ClientUtils.getWJLoginHelper();
        helper.JDLoginWithPassword("13802751849", MD5.encrypt32("boox8686"), null, true, new OnLoginCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess: ");
                countDownLatch.countDown();
            }

            @Override
            public void onError(String errorJson) {
                Log.d(TAG, "onError: " + errorJson);
                countDownLatch.countDown();
            }

            @Override
            public void onFail(FailResult failResult, PicDataInfo picDataInfo) {
                Log.d(TAG, "onFail: ");
                countDownLatch.countDown();
            }

            @Override
            public void onFail(FailResult failResult, JumpResult jumpResult, PicDataInfo picDataInfo) {
                Log.d(TAG, "onFail: ");
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testSyncLogin() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseRequestBean requestBean = new BaseRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        final RxRequestSyncLoginInfo rq = new RxRequestSyncLoginInfo();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                SyncLoginInfoBean syncLoginInfoBean = rq.getSyncLoginInfoBean();
                assertNotNull(syncLoginInfoBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
            }
        });
        countDownLatch.await();
    }

    public void testGetOrderUrl() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GetOrderRequestBean requestBean = new GetOrderRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        List<String> ids = new ArrayList<>();
        ids.add("30104683");
        String tokenBody = getTokenBody(ids);
        requestBean.setBody(tokenBody);

        final RxGetOrderUrlRequest rq = new RxGetOrderUrlRequest();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetOrderUrlResultBean orderUrlResultBean = rq.getOrderUrlResultBean();
                assertNotNull(orderUrlResultBean);
                assertEquals(0, orderUrlResultBean.getCode());
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private String getTokenBody(List<String> list) {
        JSONObject json = new JSONObject();
        try {
            JSONArray bodyJsonArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                JSONObject tempItemObject = new JSONObject();
                tempItemObject.put(CloudApiContext.GotoOrder.NUM, CloudApiContext.GotoOrder.PURCHASE_QUANTITY);
                tempItemObject.put(CloudApiContext.GotoOrder.ID, list.get(i));
                bodyJsonArray.add(i, tempItemObject);
            }
            json.put(CloudApiContext.GotoOrder.THESKUS, bodyJsonArray);
            json.put(CloudApiContext.GotoOrder.SINGLE_UNION_ID, "");
            json.put(CloudApiContext.GotoOrder.SINGLE_SUB_UNION_ID, "");
            json.put(CloudApiContext.GotoOrder.IS_SUPPORT_JS, CloudApiContext.GotoOrder.BOOLEAN);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
