package com.onyx.einfo.events;

import com.onyx.einfo.model.StorageItemViewModel;

/**
 * Created by suicheng on 2017/9/11.
 */

public class StorageItemViewModelLongClickEvent {
    public StorageItemViewModel model;

    public StorageItemViewModelLongClickEvent(StorageItemViewModel model) {
        this.model = model;
    }
}
