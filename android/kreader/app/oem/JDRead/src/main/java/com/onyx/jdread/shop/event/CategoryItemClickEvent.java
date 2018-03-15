package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;

/**
 * Created by jackdeng on 2017/12/16.
 */

public class CategoryItemClickEvent {

    private int position;
    private CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBean;

    public CategoryItemClickEvent(CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo categoryBean, int position) {
        this.categoryBean = categoryBean;
        this.position = position;
    }

    public CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo getCategoryBean() {
        return categoryBean;
    }

    public int getPosition() {
        return position;
    }
}