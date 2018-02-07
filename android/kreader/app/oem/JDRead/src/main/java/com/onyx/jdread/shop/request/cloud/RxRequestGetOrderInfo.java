package com.onyx.jdread.shop.request.cloud;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.common.EncryptHelper;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.GetOrderInfoRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.common.ReadContentService;
import com.onyx.jdread.shop.model.ShopDataBundle;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2018/1/20.
 */

public class RxRequestGetOrderInfo extends RxBaseCloudRequest {
    private BaseShopRequestBean baseShopRequestBean;
    private GetOrderInfoResultBean resultBean;
    private String[] bookIds;
    private String saltValue;

    public GetOrderInfoResultBean getResultBean() {
        return resultBean;
    }

    public void setBookIds(String[] bookIds) {
        this.bookIds = bookIds;
    }

    public void setSaltValue(String saltValue) {
        this.saltValue = saltValue;
    }

    @Override
    public Object call() throws Exception {
        handlerParams();
        encryptParams();
        ReadContentService service = CloudApiContext.getServiceForString(CloudApiContext.JD_BOOK_SHOP_URL);
        Call<String> call = getCall(service);
        Response<String> response = call.execute();
        if (response.isSuccessful()) {
            String body = response.body();
            String decryptContent = EncryptHelper.getDecryptContent(body);
            resultBean = JSONObject.parseObject(decryptContent, GetOrderInfoResultBean.class);
            checkRequestResult();
        }

        return this;
    }

    private void handlerParams() {
        baseShopRequestBean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.removeApp();
        String signValue = baseInfo.getSignValue(CloudApiContext.GotoOrder.ORDER_STEPONE);
        baseInfo.setSign(signValue);
        baseShopRequestBean.setBaseInfo(baseInfo);
        RequestBody requestBody = getRequestBody(bookIds);
        baseShopRequestBean.setBody(requestBody);
    }

    private void encryptParams() {
        String encryptKey = EncryptHelper.getEncryptKey(saltValue);
        JDAppBaseInfo appBaseInfo = baseShopRequestBean.getBaseInfo();
        String encryptParams = EncryptHelper.getEncryptParams(encryptKey, appBaseInfo.getRequestParams());
        appBaseInfo.clear();
        appBaseInfo.setEnc();
        appBaseInfo.addApp();
        appBaseInfo.setParams(encryptParams);
    }

    private RequestBody getRequestBody(String[] bookIds) {
        List<GetOrderInfoRequestBean> requestBeanList = new ArrayList<>();
        if (bookIds != null && bookIds.length > 0) {
            for (String bookId : bookIds) {
                GetOrderInfoRequestBean requestBean = new GetOrderInfoRequestBean(bookId);
                requestBeanList.add(requestBean);
            }
        }
        String requestBodyStr = JSON.toJSONString(requestBeanList);
        String encryptKey = EncryptHelper.getEncryptKey(saltValue);
        String encryptParams = EncryptHelper.getEncryptParams(encryptKey, requestBodyStr);
        return RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), encryptParams);
    }

    private void checkRequestResult() {
        if (resultBean != null && resultBean.result_code != 0) {
            ShopDataBundle.getInstance().getEventBus().post(new RequestFailedEvent(resultBean.message));
        }
    }

    private Call<String> getCall(ReadContentService service) {
        return service.getOrderInfo(baseShopRequestBean.getBaseInfo().getRequestParamsMap(),
                baseShopRequestBean.getBody());
    }
}
