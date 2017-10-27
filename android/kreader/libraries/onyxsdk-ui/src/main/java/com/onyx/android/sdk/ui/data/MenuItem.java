package com.onyx.android.sdk.ui.data;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.SparseArray;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by lxm on 2017/9/5.
 */

public class MenuItem extends BaseObservable {

    private static final String TAG = "MenuItem";

    public ObservableBoolean checked = new ObservableBoolean();
    public ObservableInt visibility = new ObservableInt(View.GONE);
    public ObservableInt menuIcon = new ObservableInt(android.R.color.transparent);
    public ObservableInt menuTextID = new ObservableInt();
    public ObservableField<String> text = new ObservableField<>();
    public ObservableBoolean enabled = new ObservableBoolean(true);
    // work when parent layout is FlexboxLayout
    public ObservableInt layoutColumns = new ObservableInt();

    private EventBus eventBus;
    private int menuId;
    private int parentMenuId;

    public MenuItem(int visibility) {
        setVisibility(visibility);
    }

    public MenuItem(int visibility, String text) {
        setVisibility(visibility);
        setText(text);
    }

    public MenuItem(int visibility, int menuIcon) {
        setVisibility(visibility);
        setMenuIcon(menuIcon);
    }


    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    public void setVisibility(int visibility) {
        this.visibility.set(visibility);
    }

    public MenuItem setMenuIcon(int menuIcon) {
        this.menuIcon.set(menuIcon);
        return this;
    }

    public MenuItem setMenuId(int menuId) {
        this.menuId = menuId;
        return this;
    }

    public MenuItem setMenuTextID(int menuTextID) {
        this.menuTextID.set(menuTextID);
        return this;
    }

    public MenuItem setEnabled(boolean enabled) {
        this.enabled.set(enabled);
        return this;
    }

    public MenuItem setLayoutColumns(int layoutColumns) {
        this.layoutColumns.set(layoutColumns);
        return this;
    }

    public MenuItem setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
        return this;
    }

    public MenuItem setParentMenuId(int parentMenuId) {
        this.parentMenuId = parentMenuId;
        return this;
    }

    public int getParentMenuId() {
        return parentMenuId;
    }

    public MenuItem setText(String text) {
        this.text.set(text);
        return this;
    }

    public static MenuItem createVisibleMenu() {
        return new MenuItem(View.VISIBLE);
    }

    public static MenuItem createSubVisibleMenu(int parentId) {
        return new MenuItem(View.VISIBLE).setParentMenuId(parentId);
    }

    public static MenuItem createVisibleMenu(String text) {
        return new MenuItem(View.VISIBLE, text);
    }

    public static MenuItem createVisibleMenu(int menuIcon) {
        return new MenuItem(View.VISIBLE, menuIcon);
    }

    public static SparseArray<MenuItem> createVisibleMenus(List<Integer> menuIds) {
        SparseArray<MenuItem> menuItems = new SparseArray<>();
        for (Integer id : menuIds) {
            menuItems.put(id, createVisibleMenu());
        }
        return menuItems;
    }

    public static SparseArray<MenuItem> createSubVisibleMenus(List<Integer> menuIds, int parentId) {
        SparseArray<MenuItem> menuItems = new SparseArray<>();
        for (Integer id : menuIds) {
            menuItems.put(id, createSubVisibleMenu(parentId));
        }
        return menuItems;
    }

    public static SparseArray<MenuItem> createVisibleMenus(List<Integer> menuIds, int columns) {
        SparseArray<MenuItem> menuItems = new SparseArray<>();
        for (Integer id : menuIds) {
            MenuItem item = new MenuItem(View.VISIBLE);
            item.setLayoutColumns(columns);
            menuItems.put(id, item);
        }
        return menuItems;
    }


    public void menuClicked(View view) {
        eventBus.post(MenuClickEvent.create(view, menuId, parentMenuId));
    }
}
