package com.onyx.jdread.library.event;

import com.onyx.android.sdk.data.model.DataModel;

/**
 * Created by hehai on 17-12-22.
 */

public class LibraryDeleteEvent {
    private DataModel dataModel;

    public LibraryDeleteEvent(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DataModel getDataModel() {
        return dataModel;
    }
}
