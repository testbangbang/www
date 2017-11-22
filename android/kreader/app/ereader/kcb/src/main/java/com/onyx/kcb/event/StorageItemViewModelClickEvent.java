package com.onyx.kcb.event;


import com.onyx.kcb.model.StorageItemViewModel;

/**
 * Created by suicheng on 2017/9/11.
 */

public class StorageItemViewModelClickEvent {
    public StorageItemViewModel model;

    public StorageItemViewModelClickEvent(StorageItemViewModel model) {
        this.model = model;
    }
}
