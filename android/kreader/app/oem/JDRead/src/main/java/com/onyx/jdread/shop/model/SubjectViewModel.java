package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
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
    private BookModelConfigResultBean.DataBean dataBean;
    private BookModelConfigResultBean.DataBean.ModulesBean modelBean;
    private BookModelConfigResultBean.DataBean.ModulesBean modelBeanNext;
    private boolean showNextTitle;
    private int index;

    public BookModelConfigResultBean.DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(BookModelConfigResultBean.DataBean dataBean, int index) {
        this.dataBean = dataBean;
        if (dataBean.ebook != null && dataBean.modules != null) {
            ArrayList<ResultBookBean> bookList = new ArrayList<>();
            BookModelConfigResultBean.DataBean.ModulesBean modulesBean = dataBean.modules.get(index);
            List<BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean> items = modulesBean.items;
            for (BookModelConfigResultBean.DataBean.ModulesBean.ItemsBean itemsBean : items) {
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

    public boolean isShowNextTitle() {
        return showNextTitle;
    }

    public void setShowNextTitle(boolean showNextTitle) {
        this.showNextTitle = showNextTitle;
        notifyChange();
    }

    public BookModelConfigResultBean.DataBean.ModulesBean getModelBeanNext() {
        return modelBeanNext;
    }

    public void setModelBeanNext(BookModelConfigResultBean.DataBean.ModulesBean modelBeanNext) {
        this.modelBeanNext = modelBeanNext;
        notifyChange();
    }

    public BookModelConfigResultBean.DataBean.ModulesBean getModelBean() {
        return modelBean;
    }

    private void setModelBean(BookModelConfigResultBean.DataBean.ModulesBean modelBean) {
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