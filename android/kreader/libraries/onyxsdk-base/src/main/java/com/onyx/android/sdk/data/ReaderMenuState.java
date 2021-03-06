package com.onyx.android.sdk.data;

/**
 * Created by joy on 6/28/16.
 */
public abstract class ReaderMenuState {
    public abstract String getTitle();
    public abstract int getPageIndex();
    public abstract int getPageCount();

    public abstract boolean canGoBack();
    public abstract boolean canGoForward();

    public abstract boolean isFixedPagingMode();
    public abstract boolean isShowingNotes();
}
