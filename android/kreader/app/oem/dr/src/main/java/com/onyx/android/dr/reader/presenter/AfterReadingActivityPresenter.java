package com.onyx.android.dr.reader.presenter;

import com.onyx.android.dr.reader.base.AfterReadingView;
import com.onyx.android.dr.reader.data.AfterReadingData;
import com.onyx.android.dr.reader.data.AfterReadingEntity;
import com.onyx.android.dr.reader.requests.RequestAfterReadingInsert;
import com.onyx.android.dr.reader.requests.RequestAfterReadingQueryDetail;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by hehai on 17-8-12.
 */

public class AfterReadingActivityPresenter {
    private AfterReadingView afterReadingView;
    private AfterReadingData afterReadingData;

    public AfterReadingActivityPresenter(AfterReadingView afterReadingView) {
        this.afterReadingView = afterReadingView;
        afterReadingData = new AfterReadingData();
    }

    public void getAfterReadingEntity(final String bookName) {
        final RequestAfterReadingQueryDetail req = new RequestAfterReadingQueryDetail(bookName);
        afterReadingData.getAfterReadingDetail(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                AfterReadingEntity noteEntity = req.getNoteEntity();
                if (noteEntity == null) {
                    noteEntity = new AfterReadingEntity();
                    noteEntity.bookName = bookName;
                }
                afterReadingView.setAfterReading(noteEntity);
            }
        });
    }

    public void saveAfterReading(AfterReadingEntity afterReadingEntity) {
        RequestAfterReadingInsert req = new RequestAfterReadingInsert(afterReadingEntity);
        afterReadingData.insertAfterReading(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {

            }
        });
    }
}
