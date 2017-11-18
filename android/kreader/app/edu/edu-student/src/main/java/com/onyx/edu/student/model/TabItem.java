package com.onyx.edu.student.model;

/**
 * Created by suicheng on 2017/7/20.
 */
public class TabItem {
    public Object extraArgument;
    public Class<?> fragmentClass;
    public String tabTitle;

    public TabItem(String tabTitle, Class<?> fragmentClass) {
        this.tabTitle = tabTitle;
        this.fragmentClass = fragmentClass;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public TabItem setExtraArgument(Object argument) {
        this.extraArgument = argument;
        return this;
    }
}
