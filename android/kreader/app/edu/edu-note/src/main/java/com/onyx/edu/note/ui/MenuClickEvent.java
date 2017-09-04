package com.onyx.edu.note.ui;

import android.view.View;

/**
 * Created by lxm on 2017/9/1.
 */

public class MenuClickEvent {

    private View view;
    private int menuAction;

    public View getView() {
        return view;
    }

    public
    int getMenuAction() {
        return menuAction;
    }

    public MenuClickEvent(View view, int menuAction) {
        this.view = view;
        this.menuAction = menuAction;
    }

    public static MenuClickEvent create(View view, int menuAction) {
        return new MenuClickEvent(view, menuAction);
    }
}
