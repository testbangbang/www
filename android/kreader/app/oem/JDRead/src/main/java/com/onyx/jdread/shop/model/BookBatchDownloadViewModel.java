package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BatchDownloadResultBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2018/3/2.
 */

public class BookBatchDownloadViewModel extends BaseObservable {

    public EventBus eventBus;
    public BatchDownloadResultBean.DataBean dataBean;

    public BookBatchDownloadViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BatchDownloadResultBean.DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(BatchDownloadResultBean.DataBean dataBean) {
        this.dataBean = dataBean;
        notifyChange();
    }
}
