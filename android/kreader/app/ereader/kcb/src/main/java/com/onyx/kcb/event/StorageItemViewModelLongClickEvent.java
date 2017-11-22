package com.onyx.kcb.event;

import com.onyx.kcb.model.StorageItemViewModel;

/**
 * Created by suicheng on 2017/9/11.
 */

public class StorageItemViewModelLongClickEvent {
    public StorageItemViewModel model;

    public StorageItemViewModelLongClickEvent(StorageItemViewModel model) {
        this.model = model;
    }
}
