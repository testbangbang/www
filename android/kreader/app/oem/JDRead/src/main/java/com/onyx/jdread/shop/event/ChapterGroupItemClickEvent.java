package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BatchDownloadResultBean;

/**
 * Created by jackdeng on 2018/1/10.
 */

public class ChapterGroupItemClickEvent {

    private BatchDownloadResultBean.DataBean.ListBean listBean;

    public ChapterGroupItemClickEvent(BatchDownloadResultBean.DataBean.ListBean listBean) {
        this.listBean = listBean;
    }
}