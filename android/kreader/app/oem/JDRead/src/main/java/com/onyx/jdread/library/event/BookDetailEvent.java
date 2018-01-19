package com.onyx.jdread.library.event;

import com.onyx.android.sdk.data.model.DataModel;

/**
 * Created by hehai on 18-1-17.
 */

public class BookDetailEvent {
    private DataModel dataModel;

    public BookDetailEvent(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DataModel getDataModel() {
        return dataModel;
    }
}
