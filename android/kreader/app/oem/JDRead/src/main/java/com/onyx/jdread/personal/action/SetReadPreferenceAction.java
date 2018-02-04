package com.onyx.jdread.personal.action;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.cloud.entity.jdbean.SetReadPreferenceBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxSetReadPreferenceRequest;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2018/1/29.
 */

public class SetReadPreferenceAction extends BaseAction {
    private List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> selectedBean;
    private List<Integer> list = new ArrayList<>();
    private SetReadPreferenceBean resultBean;

    public SetReadPreferenceAction(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> selectedBean) {
        this.selectedBean = selectedBean;
    }

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        BaseShopRequestBean bean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.READ_PREFERENCE);
        baseInfo.setSign(signValue);
        bean.setBaseInfo(baseInfo);

        List<Integer> selectedId = getSelectedId();
        String s = JSON.toJSONString(selectedId);
        RequestBody requestBody = RequestBody.create(MediaType.parse(Constants.PARSE_JSON_TYPE), s);
        bean.setBody(requestBody);
        final RxSetReadPreferenceRequest rq = new RxSetReadPreferenceRequest();
        rq.setRequestBean(bean);
        rq.execute(new RxCallback() {

            @Override
            public void onNext(Object o) {
                resultBean = rq.getResultBean();
                RxCallback.invokeNext(rxCallback, SetReadPreferenceAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
                RxCallback.invokeError(rxCallback, throwable);
            }
        });
    }

    private List<Integer> getSelectedId() {
        if (list.size() > 0) {
            list.clear();
        }
        if (selectedBean != null && selectedBean.size() > 0) {
            for (int i = 0; i < selectedBean.size(); i++) {
                CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBeanLevelTwo = selectedBean.get(i);
                list.add(categoryBeanLevelTwo.id);
            }
        }
        return list;
    }

    public SetReadPreferenceBean getResultBean() {
        return resultBean;
    }
}
