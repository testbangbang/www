package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.BookReportData;
import com.onyx.android.dr.interfaces.BookReportView;

/**
 * Created by li on 2017/9/19.
 */

public class BookReportPresenter {
    private BookReportView bookReportView;
    private BookReportData bookReportData;

    public BookReportPresenter(BookReportView bookReportView) {
        this.bookReportView = bookReportView;
        bookReportData = new BookReportData();
    }

    //public void creat
}
