package com.onyx.jdread.reader.common;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
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

    public static float calculateReadingProgress(int currentPage,int totalPage){
        float total = totalPage - 1;
        float progress = (currentPage / total) * 100;
        progress = (float) (Math.round(progress * 100)) / 100.0f;
        if(progress > 100.0f){
            progress = 100;
        }
        return progress;
    }

    public static String getChapterName(ReaderDataHolder readerDataHolder) {
        String bookName = readerDataHolder.getReaderViewInfo().getChapterName();
        return bookName;
    }

    public static String getReadProgress(ReaderViewInfo readerViewInfo){
        String readProgress = ResManager.getString(R.string.reader_loading);
        if(readerViewInfo.isLoadComplete()) {
            float progress = readerViewInfo.getProgress();
            readProgress = String.format("%.2f", progress) + "%";
        }
        return readProgress;
    }
}
