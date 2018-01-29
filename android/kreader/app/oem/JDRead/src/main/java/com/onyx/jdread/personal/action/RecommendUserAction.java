package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.personal.cloud.entity.jdbean.RecommendItemBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.RecommendUserBean;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.cloud.RxRecommendUserRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.List;

/**
 * Created by li on 2018/1/26.
 */

public class RecommendUserAction extends BaseAction {
    private List<RecommendItemBean> recommendItems;

    @Override
    public void execute(final PersonalDataBundle dataBundle, final RxCallback rxCallback) {
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setPageSize("1", "20");
        String signValue = baseInfo.getSignValue(CloudApiContext.User.RECOMMEND_USER);
        baseInfo.setSign(signValue);
        final RxRecommendUserRequest rq = new RxRecommendUserRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                RecommendUserBean recommendUserBean = rq.getRecommendUserBean();
                if (recommendUserBean != null && recommendUserBean.data != null) {
                    recommendItems = recommendUserBean.data.items;
                }
                if (rxCallback != null) {
                    rxCallback.onNext(RecommendUserAction.class);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
            }
        });
    }

    public List<RecommendItemBean> getRecommendItems() {
        return recommendItems;
    }
}
