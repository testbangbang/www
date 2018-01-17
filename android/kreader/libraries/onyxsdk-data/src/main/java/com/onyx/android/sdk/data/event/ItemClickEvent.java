package com.onyx.android.sdk.data.event;


import com.onyx.android.sdk.data.model.DataModel;

/**
 * Created by hehai on 17-11-15.
 */

public class ItemClickEvent {
    private DataModel model;
    private boolean isLayoutClicked;

    public ItemClickEvent(DataModel model, boolean isLayoutClicked) {
        this.model = model;
        this.isLayoutClicked = isLayoutClicked;
    }

    public DataModel getModel() {
        return model;
    }

    public boolean isLayoutClicked() {
        return isLayoutClicked;
    }
}
