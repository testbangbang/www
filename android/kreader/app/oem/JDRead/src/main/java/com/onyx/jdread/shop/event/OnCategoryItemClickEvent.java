package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean.CatListBean;

/**
 * Created by jackdeng on 2017/12/16.
 */

public class OnCategoryItemClickEvent {

    private CatListBean categoryBean;

    public OnCategoryItemClickEvent(CatListBean categoryBean) {
        this.categoryBean = categoryBean;
    }

    public CatListBean getCategoryBean() {
        return categoryBean;
    }
}