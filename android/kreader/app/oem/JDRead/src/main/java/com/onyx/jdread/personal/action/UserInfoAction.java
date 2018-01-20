package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxRequestUserInfo;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class UserInfoAction extends BaseAction {
    private UserInfoBean userInfoBean;

    @Override
    public void execute(PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.removeApp();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.GET_USER_INFO);
        baseInfo.setSign(signValue);
        final RxRequestUserInfo rq = new RxRequestUserInfo();
        rq.setUserInfoRequestBean(baseInfo);
        rq.setSaltValue(dataBundle.getSalt());
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
}
