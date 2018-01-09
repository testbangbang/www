package com.onyx.jdread.personal.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.personal.cloud.entity.GetOrderRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxGetOrderUrlRequest;
import com.onyx.jdread.main.common.CloudApiContext;

import java.util.List;

/**
 * Created by li on 2017/12/30.
 */

public class GetOrderUrlAction extends BaseAction {
    private List<String> ids;

    public GetOrderUrlAction(List<String> ids) {
        this.ids = ids;
    }

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        GetOrderRequestBean requestBean = new GetOrderRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        String tokenBody = getTokenBody(ids);
        requestBean.setBody(tokenBody);

        final RxGetOrderUrlRequest rq = new RxGetOrderUrlRequest();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetOrderUrlResultBean orderUrlResultBean = rq.getOrderUrlResultBean();
                if (rxCallback != null && orderUrlResultBean != null && orderUrlResultBean.getCode() == 0) {
                    dataBundle.setOrderUrlResultBean(orderUrlResultBean);
                    rxCallback.onNext(GetOrderUrlAction.class);
                }
            }
        });
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
