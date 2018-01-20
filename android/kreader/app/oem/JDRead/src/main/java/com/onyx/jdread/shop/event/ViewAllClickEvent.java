package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

/**
 * Created by jackdeng on 2018/1/9.
 */

public class ViewAllClickEvent {

    public BookModelConfigResultBean.DataBean.ModulesBean modulesBean;

    public ViewAllClickEvent(BookModelConfigResultBean.DataBean.ModulesBean modulesBean) {
        this.modulesBean = modulesBean;
    }
}