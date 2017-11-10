package com.onyx.android.dr.reader.common;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ReadPageInfo {
    public static int number = 0;
    public static int lastPage = 0;
    public static long lastTime = 0;

    public static String getReadProgress(ReaderPresenter readerPresenter){
        int pageCount = readerPresenter.getReader().getNavigator().getTotalPage();
        String pageName = readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName();
        int currentPage = Integer.parseInt(pageName);
        if (number == 0) {
            lastPage = currentPage;
            long currentTimeMillis = TimeUtils.getCurrentTimeMillis();
            lastTime = currentTimeMillis;
            number++;
        }
        DRApplication.getInstance().setCurrentPage(currentPage);
        String progress = String.format("%d/%d", currentPage + 1, pageCount);
        DRApplication.getInstance().setProgress(progress);
        DRApplication.getInstance().setPath(readerPresenter.getReader().getDocumentPath());
        return progress;
    }

    public static String getReadProgress(ReaderPresenter readerPresenter,int currentPage){
        if (number == 0) {
            lastPage = currentPage;
            long currentTimeMillis = TimeUtils.getCurrentTimeMillis();
            lastTime = currentTimeMillis;
            number++;
        }
        DRApplication.getInstance().setCurrentPage(currentPage);
        int pageCount = readerPresenter.getReader().getNavigator().getTotalPage();
        String progress = String.format("%d/%d", currentPage + 1, pageCount);
        DRApplication.getInstance().setProgress(progress);
        DRApplication.getInstance().setPath(readerPresenter.getReader().getDocumentPath());
        return progress;
    }

    public static void setReadTime(){
        long currentTimeMillis = TimeUtils.getCurrentTimeMillis();
        DRApplication.getInstance().setTime(currentTimeMillis);
    }

    public static String getReadBookName(ReaderPresenter readerPresenter){
        String bookName = readerPresenter.getBookInfo().getBookName();
        if(StringUtils.isNullOrEmpty(bookName)) {
            bookName = readerPresenter.getReader().getReaderHelper().getBookName();
            if (StringUtils.isNullOrEmpty(bookName)) {
                bookName = readerPresenter.getReader().getDocumentPath();
                bookName = FileUtils.getFileName(bookName);
            }
        }
        return bookName;
    }

    public static int getTotalPage(ReaderPresenter readerPresenter){
        return readerPresenter.getReader().getNavigator().getTotalPage();
    }

    public static int getCurrentPage(ReaderPresenter readerPresenter){
        String pageName = readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName();
        int currentPage = Integer.parseInt(pageName);
        return currentPage;
    }

    public static boolean supportNoteExport(ReaderPresenter readerPresenter) {
        String documentPath = readerPresenter.getReader().getDocumentPath();
        if (StringUtils.isNullOrEmpty(documentPath) ||
                !documentPath.toLowerCase().endsWith(".pdf")) {
            return false;
        }
        return true;
    }

    public static boolean supportTextPage(ReaderPresenter readerPresenter) {
        return readerPresenter.getReaderViewInfo() != null && readerPresenter.getReaderViewInfo().supportTextPage;
    }

    public static boolean isFixedPageDocument(ReaderPresenter readerPresenter) {
        return readerPresenter.getReaderViewInfo() != null && readerPresenter.getReaderViewInfo().isFixedDocument;
    }

    public static boolean supportTypefaceAdjustment(ReaderPresenter readerPresenter) {
        return readerPresenter.getReaderViewInfo() != null && readerPresenter.getReaderViewInfo().supportTypefaceAdjustment;
    }
}
