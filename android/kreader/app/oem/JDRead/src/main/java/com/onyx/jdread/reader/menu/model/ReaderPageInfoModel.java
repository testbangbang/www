package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemNextChapterEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemPreviousChapterEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderPageInfoModel {
    private ObservableField<String> bookName = new ObservableField<>();
    private ObservableField<String> readProgress = new ObservableField<>();
    private ObservableBoolean isShow = new ObservableBoolean(true);

    public ObservableField<String> getBookName() {
        return bookName;
    }

    public ObservableField<String> getReadProgress() {
        return readProgress;
    }

    public void nextChapter(){
        EventBus.getDefault().post(new ReaderSettingMenuItemNextChapterEvent());
    }

    public void previousChapter(){
        EventBus.getDefault().post(new ReaderSettingMenuItemPreviousChapterEvent());
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }
}
