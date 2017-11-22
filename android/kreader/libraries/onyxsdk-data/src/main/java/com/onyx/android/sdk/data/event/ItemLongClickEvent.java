package com.onyx.android.sdk.data.event;


import com.onyx.android.sdk.data.model.DataModel;

/**
 * Created by hehai on 17-11-16.
 */

public class ItemLongClickEvent {
    private DataModel dataModel;

    public ItemLongClickEvent(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DataModel getDataModel() {
        return dataModel;
    }
}
