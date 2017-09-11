package com.onyx.edu.note.ui;

import com.onyx.android.sdk.ui.view.PageRecyclerView;

/**
 * Created by lxm on 2017/9/2.
 */

public class FunctionMenuClickEvent {

    private int menuId;

    public int getMenuId() {
        return menuId;
    }

    public FunctionMenuClickEvent(int menuId) {
        this.menuId = menuId;
    }
}
