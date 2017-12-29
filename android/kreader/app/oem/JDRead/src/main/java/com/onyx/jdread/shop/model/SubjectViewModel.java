package com.onyx.jdread.shop.model;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class SubjectViewModel {
    private BookModelResultBean modelBean;

    public BookModelResultBean getModelBean() {
        return modelBean;
    }

    public void setModelBean(BookModelResultBean modelBean) {
        this.modelBean = modelBean;
    }

    public void onViewAllClick() {

    }
}