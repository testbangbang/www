package com.onyx.edu.note.ui;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * Created by lxm on 2017/9/5.
 */

public class Menu {

    private ViewDataBinding menuBinding;

    private Map<Integer, MenuItem> itemMap;

    public static Menu create(final Context context,
                              final EventBus eventBus,
                              final int layoutId,
                              final Map<Integer, MenuItem> itemMap) {
        Menu menu = new Menu();
        menu.itemMap = itemMap;
        for (Integer key : itemMap.keySet()) {
            itemMap.get(key).setMenuId(key).setEventBus(eventBus);
        }
        menu.menuBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false);
        menu.menuBinding.setVariable(BR.item, itemMap);
        return menu;
    }

    public Menu show(final ViewGroup parent, final ViewGroup.LayoutParams params) {
        parent.addView(getRootView(), params);
        return this;
    }

    public Menu check(int menuId) {
        itemMap.get(menuId).setChecked(true);
        executePendingBindings();
        return this;
    }

    public Menu setText(int menuId, String text) {
        itemMap.get(menuId).setText(text);
        executePendingBindings();
        return this;
    }

    public Menu unCheckAll() {
        for (MenuItem menuItem : itemMap.values()) {
            menuItem.setChecked(false);
        }
        executePendingBindings();
        return this;
    }

    public Menu executePendingBindings() {
        getMenuBinding().executePendingBindings();
        return this;
    }

    public ViewDataBinding getMenuBinding() {
        return menuBinding;
    }

    public View getRootView() {
        return getMenuBinding().getRoot();
    }
}
