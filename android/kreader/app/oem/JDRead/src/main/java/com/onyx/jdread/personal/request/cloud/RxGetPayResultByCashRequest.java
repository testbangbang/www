package com.onyx.jdread.personal.request.cloud;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderStatusBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;

/**
 * Created by suicheng on 2018/2/28.
 */
public class RxGetPayResultByCashRequest extends RxBaseCloudRequest {

    private JDAppBaseInfo requestBean;
    private String token;
    private GetOrderStatusBean<Boolean> orderStatusBean;

    private long totalTime = 5 * 60 * 1000;
    private long firstDelayTime = 30 * 1000;
    private long secondPeriod = 4 * 1000;
    private long secondTotalTime = 3 * 60 * 1000;
    private long thirdPeriod = 30 * 1000;

    public RxGetPayResultByCashRequest(String token) {
        this.token = token;
        initTime();
    }

    private void initTime() {
        totalTime = ResManager.getInteger(R.integer.pay_by_cash_total_time);
        firstDelayTime = ResManager.getInteger(R.integer.pay_by_cash_first_delay_time);
        secondTotalTime = ResManager.getInteger(R.integer.pay_by_cash_second_total_time);
        secondPeriod = ResManager.getInteger(R.integer.pay_by_cash_second_period_time);
        thirdPeriod = ResManager.getInteger(R.integer.pay_by_cash_third_period_time);
    }

    public GetOrderStatusBean<Boolean> getOrderStatusBean() {
        return orderStatusBean;
    }

    @Override
    public RxGetPayResultByCashRequest call() throws Exception {
        boolean result = runStageOne();
        if (result || getAbort()) {
            return this;
        }
        result = runStageTwo();
        if (result || getAbort()) {
            return this;
        }
        runStageThree();
        return this;
    }

    private boolean runStageOne() {
        TestUtils.sleep(firstDelayTime);
        orderStatusBean = fetchOrderStatus();
        return checkResult(orderStatusBean);
    }

    private boolean runStageTwo() {
        return fetchPayStatusByPeriod(secondTotalTime, secondPeriod);
    }

    private boolean runStageThree() {
        long time = totalTime - firstDelayTime - secondTotalTime;
        if (time < 0) {
            time = thirdPeriod;
        }
        return fetchPayStatusByPeriod(time, thirdPeriod);
    }

    private boolean fetchPayStatusByPeriod(long totalTime, long period) {
        long count = totalTime / period;
        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
        while (count-- > 0) {
            if (getAbort()) {
                return false;
            }
            orderStatusBean = fetchOrderStatus();
            if (checkResult(orderStatusBean)) {
                return true;
            }
            long time = System.currentTimeMillis();
            long diff = time - currentTime;
            currentTime = time;
            if (diff < period) {
                TestUtils.sleep(diff);
            }
            if (System.currentTimeMillis() - startTime > totalTime) {
                return false;
            }
        }
        return false;
    }

    public boolean checkResult(GetOrderStatusBean<Boolean> statusBean) {
        return GetOrderStatusBean.checkSuccess(statusBean) && statusBean.data;
    }

    private GetOrderStatusBean<Boolean> fetchOrderStatus() {
        GetOrderStatusBean<Boolean> statusBean;
        try {
            Response<GetOrderStatusBean<Boolean>> response = CloudApiContext.getService(CloudApiContext.getJDBooxBaseUrl())
                    .payByCash(getQueryMap()).execute();
            statusBean = response.body();
        } catch (Exception ignored) {
            statusBean = null;
        }
        return statusBean;
    }

    private Map<String, String> getQueryMap() {
        if (requestBean != null) {
            requestBean.getRequestParamsMap();
        }
        Map<String, String> map = new HashMap<>();
        map.put(CloudApiContext.ReadBean.PAY_TOKEN, token);
        requestBean = new JDAppBaseInfo(map);
        requestBean.setSign(requestBean.getSignValue(CloudApiContext.GotoOrder.ORDER_STATUS));
        return requestBean.getRequestParamsMap();
    }
}
