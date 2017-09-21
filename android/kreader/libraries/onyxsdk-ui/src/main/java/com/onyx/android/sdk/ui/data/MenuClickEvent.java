package com.onyx.android.sdk.ui.data;

import android.view.View;

/**
 * Created by lxm on 2017/9/1.
 */

public class MenuClickEvent {

    private View view;
    private int menuId;
    private int parentId;

    public View getView() {
        return view;
    }

    public int getMenuId() {
        return menuId;
    }

    public int getParentId() {
        return parentId;
    }

    public boolean isSubMenu() {
        return parentId > 0;
    }

    public MenuClickEvent(View view, int menuId, int parentId) {
        this.view = view;
        this.menuId = menuId;
        this.parentId = parentId;
    }

    public MenuClickEvent(View view, int menuId) {
        this.view = view;
        this.menuId = menuId;
    }

    public static MenuClickEvent create(View view, int menuId, int parentId) {
        return new MenuClickEvent(view, menuId, parentId);
    }
}
