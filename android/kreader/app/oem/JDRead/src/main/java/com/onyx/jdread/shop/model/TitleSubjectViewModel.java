package com.onyx.jdread.shop.model;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class TitleSubjectViewModel extends BaseSubjectViewModel {

    private List<BookModelConfigResultBean.DataBean.ModulesBean> tilteList;

    public TitleSubjectViewModel(EventBus eventBus) {
        setEventBus(eventBus);
        setSubjectType(SubjectType.TYPE_TITLE);
    }

    public List<BookModelConfigResultBean.DataBean.ModulesBean> getTilteList() {
        return tilteList;
    }

    public void setTilteList(List<BookModelConfigResultBean.DataBean.ModulesBean> tilteList) {
        this.tilteList = tilteList;
    }

}