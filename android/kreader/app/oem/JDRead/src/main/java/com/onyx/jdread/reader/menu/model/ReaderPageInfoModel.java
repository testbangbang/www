package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableField;

import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemNextChapterEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemPreviousChapterEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderPageInfoModel {
    private final ObservableField<String> bookName = new ObservableField<>();
    private final ObservableField<String> readProgress = new ObservableField<>();

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
}
