package com.onyx.edu.note.ui;

import android.view.View;

import com.onyx.android.sdk.ui.data.MenuAction;

/**
 * Created by lxm on 2017/9/1.
 */

public class MenuClickEvent {

    private View view;
    private @MenuAction.ActionDef
    int menuAction;

    public View getView() {
        return view;
    }

    public @MenuAction.ActionDef
    int getMenuAction() {
        return menuAction;
    }

    public MenuClickEvent(View view, @MenuAction.ActionDef int menuAction) {
        this.view = view;
        this.menuAction = menuAction;
    }

    public static MenuClickEvent create(View view, @MenuAction.ActionDef int menuAction) {
        return new MenuClickEvent(view, menuAction);
    }
}
