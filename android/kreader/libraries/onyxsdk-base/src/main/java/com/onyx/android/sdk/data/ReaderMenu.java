package com.onyx.android.sdk.data;

import java.util.List;

/**
 * Created by Joy on 2016/4/19.
 */
public abstract class ReaderMenu {
    public static abstract class ReaderMenuCallback {
        public abstract void onMenuItemClicked(ReaderMenuItem menuItem);
        public abstract void onMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue);
        public abstract void onHideMenu();
    }

    private ReaderMenuCallback callback = null;
    private boolean fullscreen;

    public abstract boolean isShown();
    public abstract void show(ReaderMenuState state);
    public abstract void hide();
    public abstract void updateReaderMenuState(ReaderMenuState state);

    public abstract void fillItems(List<? extends ReaderMenuItem> items);

    public void setReaderMenuCallback(ReaderMenuCallback callback) {
        this.callback = callback;
    }

    public void notifyMenuItemClicked(ReaderMenuItem menuItem) {
        if (callback != null) {
            callback.onMenuItemClicked(menuItem);
        }
    }

    public void notifyMenuItemValueChanged(ReaderMenuItem menuItem, Object oldValue, Object newValue) {
        if (callback != null) {
            callback.onMenuItemValueChanged(menuItem, oldValue, newValue);
        }
    }

    public void notifyHideMenu() {
        if (callback != null) {
            callback.onHideMenu();
        }
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullScreen(boolean fullscreen) {
        if (this.fullscreen != fullscreen) {
            fullScreenStateChange(fullscreen);
        }
        this.fullscreen = fullscreen;
    }

    public void fullScreenStateChange(boolean fullscreen) {
    }
}
