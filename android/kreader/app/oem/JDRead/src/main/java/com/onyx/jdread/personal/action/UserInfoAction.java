package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.personal.cloud.entity.UserInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxRequestUserInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class UserInfoAction extends BaseAction {

    private String pin;
    private UserInfoBean userInfoBean;

    public UserInfoAction(String pin) {
        this.pin = pin;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        UserInfoRequestBean requestBean = new UserInfoRequestBean();
        requestBean.setAppBaseInfo(dataBundle.getAppBaseInfo());
        String userInfoJsonBody = getUserInfoJsonBody(pin);
        requestBean.setBody(userInfoJsonBody);
        final RxRequestUserInfo rq = new RxRequestUserInfo();
        rq.setUserInfoRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                userInfoBean = rq.getUserInfoBean();
                if(rxCallback != null){
                    rxCallback.onNext(UserInfoAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if(rxCallback != null){
                    rxCallback.onError(throwable);
                }
            }
        });
    }

    public UserInfoBean getUserInfoData() {
        return userInfoBean;
    }

    private String getUserInfoJsonBody(String pin) {
        final JSONObject json = new JSONObject();
        try {
            json.put(Constants.SP_KEY_LIST_PIN, pin);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
