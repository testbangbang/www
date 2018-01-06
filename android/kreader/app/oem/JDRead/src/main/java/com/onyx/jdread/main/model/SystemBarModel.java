package com.onyx.jdread.main.model;

import android.databinding.ObservableBoolean;

/**
 * Created by huxiaomao on 2017/12/9.
 */

public class SystemBarModel {
    private ObservableBoolean isShow = new ObservableBoolean(true);

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }
}
