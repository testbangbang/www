package com.onyx.android.dr.reader.event;

import android.content.Context;

import com.onyx.android.dr.reader.presenter.ReaderPresenter;

/**
 * Created by zhuzeng on 08/01/2017.
 */

public class PageChangedEvent {

    private String lastPage;
    private String currentPage;
    private long duration;
    private Context context;

    public PageChangedEvent() {
    }

    public static final PageChangedEvent beforePageChange(final ReaderPresenter readerDataHolder) {
        final PageChangedEvent pageChangedEvent = new PageChangedEvent();

        pageChangedEvent.setLastPage(readerDataHolder.getReaderViewInfo().getFirstVisiblePageName());
        pageChangedEvent.setContext(readerDataHolder.getReaderView().getViewContext());
        return pageChangedEvent;
    }

    public void afterPageChange(final ReaderPresenter readerDataHolder) {
        setCurrentPage(readerDataHolder.getReaderViewInfo().getFirstVisiblePageName());
    }

    public String getLastPage() {
        return lastPage;
    }

    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
