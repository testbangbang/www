package com.onyx.jdread.reader.common;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/1/5.
 */

public class ReaderPageInfoFormat {
    public static float getReadProgress(ReaderDataHolder readerDataHolder, ReaderViewInfo readerViewInfo) {
        int currentPage = readerDataHolder.getCurrentPage();
        float total = readerViewInfo.getTotalPage() - 1;
        float progress = (currentPage / total) * 100;
        return (float) (Math.round(progress * 100)) / 100;
    }

    public static String getChapterName(ReaderDataHolder readerDataHolder) {
        String bookName = readerDataHolder.getReader().getDocumentInfo().getBookName();
        return bookName;
    }
}
