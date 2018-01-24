package com.onyx.jdread.reader.menu.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.GotoPageEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemNextChapterEvent;
import com.onyx.jdread.reader.menu.event.ReaderSettingMenuItemPreviousChapterEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderPageInfoModel {
    private ObservableField<String> bookName = new ObservableField<>();
    private ObservableField<String> readProgress = new ObservableField<>();
    private ObservableInt pageTotal = new ObservableInt(0);
    private ObservableInt currentPage = new ObservableInt(0);
    private ObservableBoolean isShow = new ObservableBoolean(true);
    private ReaderDataHolder readerDataHolder;

    public ReaderPageInfoModel(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public ObservableField<String> getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName.set(bookName);
    }

    public ObservableField<String> getReadProgress() {
        return readProgress;
    }

    public void setReadProgress(String readProgress) {
        this.readProgress.set(readProgress);
    }

    public ObservableInt getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal.set(pageTotal);
    }

    public ObservableInt getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage.set(currentPage);
    }

    public void nextChapter(){
        readerDataHolder.getEventBus().post(new ReaderSettingMenuItemNextChapterEvent());
    }

    public void previousChapter(){
        readerDataHolder.getEventBus().post(new ReaderSettingMenuItemPreviousChapterEvent());
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow.set(isShow);
    }
}
