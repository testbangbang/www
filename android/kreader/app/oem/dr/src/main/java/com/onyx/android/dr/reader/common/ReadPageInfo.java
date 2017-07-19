package com.onyx.android.dr.reader.common;

import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by huxiaomao on 17/5/12.
 */

public class ReadPageInfo {
    public static String getReadProgress(ReaderPresenter readerPresenter){
        int pageCount = readerPresenter.getReader().getNavigator().getTotalPage();
        String pageName = readerPresenter.getReaderViewInfo().getFirstVisiblePage().getName();
        int currentPage = Integer.parseInt(pageName);
        return String.format("%d/%d", currentPage + 1, pageCount);
    }

    public static String getReadProgress(ReaderPresenter readerPresenter,int currentPage){
        int pageCount = readerPresenter.getReader().getNavigator().getTotalPage();
        return String.format("%d/%d", currentPage + 1, pageCount);
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
