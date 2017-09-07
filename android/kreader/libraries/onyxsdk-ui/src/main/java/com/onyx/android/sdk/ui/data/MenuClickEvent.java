package com.onyx.android.sdk.ui.data;

import android.view.View;

/**
 * Created by lxm on 2017/9/1.
 */

public class MenuClickEvent {

    private View view;
    private int menuId;

    public View getView() {
        return view;
    }

    public int getMenuId() {
        return menuId;
    }

    public MenuClickEvent(View view, int menuId) {
        this.view = view;
        this.menuId = menuId;
    }

    public static MenuClickEvent create(View view, int menuAction) {
        return new MenuClickEvent(view, menuAction);
    }
}
