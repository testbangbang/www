package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class BannerViewModel extends BaseObservable {

    private BookModelConfigResultBean.DataBean dataBean;
    private List<BookModelConfigResultBean.DataBean.AdvBean> bannerList;
    private EventBus eventBus;

    public BookModelConfigResultBean.DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(BookModelConfigResultBean.DataBean dataBean) {
        this.dataBean = dataBean;
        parseAdvBeanList(dataBean);
    }

    private void parseAdvBeanList(BookModelConfigResultBean.DataBean dataBean) {
        bannerList = new ArrayList<>();
        if (dataBean.modules != null && dataBean.ebook != null) {
            List<BookModelConfigResultBean.DataBean.ModulesBean> bannerModules = dataBean.modules.subList(0, Constants.SHOP_MAIN_INDEX_TWO);
            for (BookModelConfigResultBean.DataBean.ModulesBean modulesBean : bannerModules) {
                List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
                for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                    BookModelConfigResultBean.DataBean.AdvBean advBean = dataBean.adv.get(itemsBean.id);
                    bannerList.add(advBean);
                }
            }
            setBannerList(bannerList);
        }
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public List<BookModelConfigResultBean.DataBean.AdvBean> getBannerList() {
        return bannerList;
    }

    private void setBannerList(List<BookModelConfigResultBean.DataBean.AdvBean> bannerList) {
        this.bannerList = bannerList;
        notifyChange();
    }
}