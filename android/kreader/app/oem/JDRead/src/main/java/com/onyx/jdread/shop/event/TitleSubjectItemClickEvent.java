package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

/**
 * Created by jackdeng on 2018/1/31.
 */

public class TitleSubjectItemClickEvent {
    public BookModelConfigResultBean.DataBean.ModulesBean modulesBean;

    public TitleSubjectItemClickEvent(BookModelConfigResultBean.DataBean.ModulesBean modulesBean) {
        this.modulesBean = modulesBean;
    }
}
