package com.onyx.jdread.reader.menu.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;

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
    private EventBus eventBus;
    private ObservableBoolean isShowDelete = new ObservableBoolean(false);

    public ObservableBoolean getIsShowDelete() {
        return isShowDelete;
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete.set(isShowDelete);
    }

    public ReaderSearchModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
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
        getEventBus().post(new SearchEvent(getSearchContent()));
    }

    public void back() {
        getEventBus().post(new CloseSearchDialogEvent());
    }
}
