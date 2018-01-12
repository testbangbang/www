package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookShopMainConfigResultBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class BannerViewModel extends BaseObservable {

    private BookShopMainConfigResultBean.DataBean dataBean;
    private List<BookShopMainConfigResultBean.DataBean.AdvBean> bannerList;
    private EventBus eventBus;

    public BookShopMainConfigResultBean.DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(BookShopMainConfigResultBean.DataBean dataBean) {
        this.dataBean = dataBean;
        parseAdvBeanList(dataBean);
    }

    private void parseAdvBeanList(BookShopMainConfigResultBean.DataBean dataBean) {
        bannerList = new ArrayList<>();
        if (dataBean.modules != null && dataBean.ebook != null) {
            List<BookShopMainConfigResultBean.DataBean.ModulesBean> bannerModules = dataBean.modules.subList(0, Constants.SHOP_MAIN_INDEX_TWO);
            for (BookShopMainConfigResultBean.DataBean.ModulesBean modulesBean : bannerModules) {
                List<BookShopMainConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
                for (BookShopMainConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                    BookShopMainConfigResultBean.DataBean.AdvBean advBean = dataBean.adv.get(itemsBean.id);
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

    public List<BookShopMainConfigResultBean.DataBean.AdvBean> getBannerList() {
        return bannerList;
    }

    private void setBannerList(List<BookShopMainConfigResultBean.DataBean.AdvBean> bannerList) {
        this.bannerList = bannerList;
        notifyChange();
    }
}