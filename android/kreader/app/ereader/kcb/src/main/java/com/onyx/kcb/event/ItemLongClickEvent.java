package com.onyx.kcb.event;

import com.onyx.kcb.model.DataModel;

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
