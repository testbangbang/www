package com.onyx.jdread.reader.menu.common;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.jdread.reader.actions.ToggleBookmarkAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/27.
 */

public class BookmarkHandle {
    public void toggleBookmarkEvent(ReaderDataHolder readerDataHolder) {
        if (hasBookmark(readerDataHolder)) {
            removeBookmark(readerDataHolder);
        } else {
            addBookmark(readerDataHolder);
        }
    }

    private void removeBookmark(ReaderDataHolder readerDataHolder) {
        ToggleBookmarkAction action = new ToggleBookmarkAction(ToggleBookmarkAction.ToggleSwitch.Off, readerDataHolder.getReaderUserDataInfo(), getFirstVisiblePageWithBookmark(readerDataHolder));
        action.execute(readerDataHolder, null);
    }

    private void addBookmark(ReaderDataHolder readerDataHolder) {
        ToggleBookmarkAction action = new ToggleBookmarkAction(ToggleBookmarkAction.ToggleSwitch.On, readerDataHolder.getReaderUserDataInfo(), getFirstPageInfo(readerDataHolder));
        action.execute(readerDataHolder, null);
    }

    public boolean hasBookmark(ReaderDataHolder readerDataHolder) {
        return getFirstVisiblePageWithBookmark(readerDataHolder) != null;
    }

    public PageInfo getFirstVisiblePageWithBookmark(ReaderDataHolder readerDataHolder) {
        for (PageInfo pageInfo : getVisiblePages(readerDataHolder)) {
            if (readerDataHolder.getReaderUserDataInfo() == null) {
                continue;
            }
            if (readerDataHolder.getReaderUserDataInfo().hasBookmark(pageInfo)) {
                return pageInfo;
            }
        }
        return null;
    }

    public final List<PageInfo> getVisiblePages(ReaderDataHolder readerDataHolder) {
        ArrayList<PageInfo> pages = new ArrayList<>();

        PageInfo firstPage = readerDataHolder.getReaderViewInfo().getFirstVisiblePage();
        if (firstPage == null) {
            return pages;
        }
        if (!supportScalable(readerDataHolder)) {
            firstPage.setSubPage(-1);
        }

        pages.add(firstPage);
        return pages;
    }

    public boolean supportScalable(ReaderDataHolder readerDataHolder) {
        return readerDataHolder.getReaderViewInfo() != null && readerDataHolder.getReaderViewInfo().supportScalable;
    }

    public final PageInfo getFirstPageInfo(ReaderDataHolder readerDataHolder) {
        return readerDataHolder.getReaderViewInfo().getFirstVisiblePage();
    }
}
