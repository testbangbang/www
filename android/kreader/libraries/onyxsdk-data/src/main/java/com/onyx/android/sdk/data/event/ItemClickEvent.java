package com.onyx.android.sdk.data.event;


import com.onyx.android.sdk.data.model.DataModel;

/**
 * Created by hehai on 17-11-15.
 */

public class ItemClickEvent {
    private DataModel model;
    public ItemClickEvent(DataModel model) {
        this.model = model;
    }

    public DataModel getModel() {
        return model;
    }
}
