package com.onyx.kreader.ui.menu;

import android.view.MenuItem;

import java.util.List;

/**
 * Created by Joy on 2016/4/19.
 */
public abstract class ReaderMenu {
    public static abstract class ReaderMenuCallback {
        public abstract void onMenuItemClicked(ReaderMenuItem menuItem);
    }

    protected ReaderMenuCallback callback = null;

    public abstract boolean isShown();
    public abstract void show();
    public abstract void hide();

    public abstract void fillItems(List<? extends ReaderMenuItem> items);

    public void setReaderMenuCallback(ReaderMenuCallback callback) {
        this.callback = callback;
    }
}
