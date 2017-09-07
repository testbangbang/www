package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.BookDetailData;
import com.onyx.android.dr.interfaces.BookDetailView;
import com.onyx.android.dr.request.cloud.RequestGetBookDetail;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by hehai on 17-9-6.
 */

public class BookDetailPresenter {
    private BookDetailView bookDetailView;
    private BookDetailData bookDetailData;

    public BookDetailPresenter(BookDetailView bookDetailView) {
        this.bookDetailView = bookDetailView;
        bookDetailData = new BookDetailData();
    }

    public void loadBookDetail(String bookId){
        final RequestGetBookDetail req = new RequestGetBookDetail(bookId);
        bookDetailData.loadBookDetail(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookDetailView.setBookDetail(req.getCloudMetadata());
            }
        });
    }
}
