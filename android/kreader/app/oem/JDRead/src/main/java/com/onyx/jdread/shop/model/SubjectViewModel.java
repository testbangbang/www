package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookShopMainConfigResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.event.ViewAllClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class SubjectViewModel extends BaseObservable {

    private EventBus eventBus;
    private BookShopMainConfigResultBean.DataBean dataBean;
    private BookShopMainConfigResultBean.DataBean.ModulesBean modelBean;
    private BookShopMainConfigResultBean.DataBean.ModulesBean modelBeanNext;
    private boolean showNextTitle;
    private int index;

    public BookShopMainConfigResultBean.DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(BookShopMainConfigResultBean.DataBean dataBean) {
        this.dataBean = dataBean;
        if (dataBean.ebook != null && dataBean.modules != null) {
            ArrayList<ResultBookBean> bookList = new ArrayList<>();
            BookShopMainConfigResultBean.DataBean.ModulesBean modulesBean = dataBean.modules.get(getIndex());
            List<BookShopMainConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
            for (BookShopMainConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
                ResultBookBean bookBean = dataBean.ebook.get(itemsBean.id);
                bookList.add(bookBean);
            }
            modulesBean.bookList = bookList;
            setModelBean(modulesBean);
        }
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isShowNextTitle() {
        return showNextTitle;
    }

    public void setShowNextTitle(boolean showNextTitle) {
        this.showNextTitle = showNextTitle;
        notifyChange();
    }

    public BookShopMainConfigResultBean.DataBean.ModulesBean getModelBeanNext() {
        return modelBeanNext;
    }

    private void setModelBeanNext(BookShopMainConfigResultBean.DataBean.ModulesBean modelBeanNext) {
        this.modelBeanNext = modelBeanNext;
        notifyChange();
    }

    public BookShopMainConfigResultBean.DataBean.ModulesBean getModelBean() {
        return modelBean;
    }

    private void setModelBean(BookShopMainConfigResultBean.DataBean.ModulesBean modelBean) {
        this.modelBean = modelBean;
        notifyChange();
    }

    public void onViewAllClick() {
        getEventBus().post(new ViewAllClickEvent(modelBean.id, modelBean.f_type, modelBean.show_name));
    }

    public void onNextViewAllClick() {
        getEventBus().post(new ViewAllClickEvent(modelBeanNext.id, modelBeanNext.f_type, modelBeanNext.show_name));
    }
}