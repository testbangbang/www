package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableInt;

import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class ViewAllViewModel extends BaseObservable {

    private EventBus eventBus;
    private TitleBarViewModel titleBarViewModel;
    public final ObservableInt currentPosition = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    public List<ResultBookBean> bookList;

    public ViewAllViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
    }

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public int getCurrentPage() {
        return currentPosition.get();
    }

    public void setCurrentPage(int curPage) {
        curPage = curPage < 1 ? 1 : curPage;
        currentPosition.set(curPage);
    }

    public int getTotalPage() {
        return totalPage.get();
    }

    public void setTotalPage(int allPage) {
        allPage = allPage < 1 ? 1 : allPage;
        totalPage.set(allPage);
    }

    public List<ResultBookBean> getBookList() {
        return bookList;
    }

    public void setBookList(List<ResultBookBean> bookList) {
        this.bookList = bookList;
        notifyChange();
    }
}