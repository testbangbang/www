package com.onyx.android.dr.reader.presenter;

import com.onyx.android.dr.reader.base.AfterReadingView;
import com.onyx.android.dr.reader.data.AfterReadingEntity;

/**
 * Created by hehai on 17-8-12.
 */

public class AfterReadingActivityPresenter {
    private AfterReadingView afterReadingView;

    public AfterReadingActivityPresenter(AfterReadingView afterReadingView) {
        this.afterReadingView = afterReadingView;
    }

    public void getAfterReadingEntity(String md5) {
        // TODO: 17-8-12
        afterReadingView.setAfterReading(null);
    }

    public void saveAfterReading(AfterReadingEntity afterReadingEntity) {
        // TODO: 17-8-12  
    }
}
