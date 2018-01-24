package com.onyx.jdread.reader.menu.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.event.CloseSearchDialogEvent;
import com.onyx.jdread.reader.menu.event.SearchEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2018/1/13.
 */

public class ReaderSearchModel extends BaseObservable {
    private String searchContent;
    private int searchSize;
    private ReaderDataHolder readerDataHolder;

    public ReaderSearchModel(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
        notifyChange();
    }

    public int getSearchSize() {
        return searchSize;
    }

    public void setSearchSize(int searchSize) {
        this.searchSize = searchSize;
        notifyChange();
    }

    public void clearInput() {
        setSearchContent("");
        setSearchSize(0);
    }

    public void search() {
        readerDataHolder.getEventBus().post(new SearchEvent(getSearchContent()));
    }

    public void back() {
        readerDataHolder.getEventBus().post(new CloseSearchDialogEvent());
    }
}
