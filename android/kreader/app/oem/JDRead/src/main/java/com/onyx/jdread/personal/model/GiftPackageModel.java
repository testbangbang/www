package com.onyx.jdread.personal.model;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.personal.cloud.entity.jdbean.CheckGiftBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GiftDetailBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/2/26.
 */

public class GiftPackageModel {
    List<GiftDetailBean> giftBeans = new ArrayList<>();
    private EventBus eventBus;

    public void loadGifts() {
        if (giftBeans.size() > 0) {
            giftBeans.clear();
        }
        GiftDetailBean detailBean = new GiftDetailBean();
        detailBean.setEventBus(eventBus);
        detailBean.packageName = ResManager.getString(R.string.new_user_package);
        detailBean.packageDetail = ResManager.getString(R.string.new_user_package_detail);
        giftBeans.add(detailBean);
    }

    public List<GiftDetailBean> getGiftBeans() {
        return giftBeans;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
