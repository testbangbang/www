package com.onyx.android.sdk.ui.data;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2017/9/5.
 */

public class Menu {

    private ViewDataBinding menuBinding;
    private SparseArray<MenuItem> itemMap;
    private boolean isShowing = false;
    private int parentMenuId;

    public static Menu addMenu(ViewGroup parent,
                               EventBus eventBus,
                               int layoutId,
                               int menuVariable,
                               ViewGroup.LayoutParams params,
                               SparseArray<MenuItem> menuItemMap) {
        Menu menu = Menu.create(parent.getContext(), eventBus, layoutId, menuVariable, menuItemMap);
        menu.show(parent, params);
        return menu;
    }

    public static Menu create(final Context context,
                              final EventBus eventBus,
                              final int layoutId,
                              final int menuVariable,
                              final SparseArray<MenuItem> itemMap) {
        Menu menu = new Menu();
        menu.itemMap = itemMap;
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            Integer key = itemMap.keyAt(i);
            itemMap.get(key).setMenuId(key).setEventBus(eventBus);
        }
        menu.menuBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false);
        menu.menuBinding.setVariable(menuVariable, itemMap);
        return menu;
    }

    public Menu show(final ViewGroup parent, final ViewGroup.LayoutParams params) {
        remove(parent);
        parent.addView(getRootView(), params);
        isShowing = true;
        return this;
    }

    public void remove(ViewGroup parent) {
        parent.removeView(getRootView());
        isShowing = false;
    }

    public Menu check(int menuId) {
        MenuItem item = itemMap.get(menuId);
        if (item != null) {
            item.setChecked(true);
        }
        return this;
    }

    public Menu setColumns(int columns) {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            itemMap.valueAt(i).setLayoutColumns(columns);
        }
        return this;
    }

    public Menu setEnabled(int menuId, boolean enabled) {
        MenuItem item = itemMap.get(menuId);
        if (item != null) {
            item.setEnabled(enabled);
        }
        return this;
    }

    public Menu setText(int menuId, String text) {
        MenuItem item = itemMap.get(menuId);
        if (item != null) {
            item.setText(text);
        }
        return this;
    }

    public Menu unCheckAll() {
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            itemMap.valueAt(i).setChecked(false);
        }
        return this;
    }

    public Menu executePendingBindings() {
        getMenuBinding().executePendingBindings();
        return this;
    }

    public void setParentMenuId(int parentMenuId) {
        this.parentMenuId = parentMenuId;
    }

    public int getParentMenuId() {
        return parentMenuId;
    }

    public boolean isShowing() {
        return isShowing && getRootView().getVisibility() == View.VISIBLE;
    }

    public ViewDataBinding getMenuBinding() {
        return menuBinding;
    }

    public View getRootView() {
        return getMenuBinding().getRoot();
    }

    public int getRootViewId() {
        return getRootView().getId();
    }

    public Menu updateItemMap(int menuVariable, EventBus eventBus, SparseArray<MenuItem> map) {
        this.itemMap = map;
        int size = itemMap.size();
        for (int i = 0; i < size; i++) {
            Integer key = itemMap.keyAt(i);
            itemMap.get(key).setMenuId(key).setEventBus(eventBus);
        }
        menuBinding.setVariable(menuVariable, itemMap);
        return this;
    }
}