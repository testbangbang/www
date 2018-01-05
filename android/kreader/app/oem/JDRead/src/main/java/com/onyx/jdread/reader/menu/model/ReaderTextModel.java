package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.R;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemBackPdfEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemCustomizeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderTextModel {
    private ObservableBoolean isShow = new ObservableBoolean(false);
    private ObservableBoolean isPdf = new ObservableBoolean(false);


    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }

    public ObservableBoolean getIsPdf() {
        return isPdf;
    }

    public void setIsPdf(boolean isPdf) {
        this.isPdf.set(isPdf);
    }

    public void onBackPDFClick(){
        EventBus.getDefault().post(new ReaderSettingMenuItemBackPdfEvent());
    }

    public void onCustomizeItemClick(){
        EventBus.getDefault().post(new ReaderSettingMenuItemCustomizeEvent());
    }
}
