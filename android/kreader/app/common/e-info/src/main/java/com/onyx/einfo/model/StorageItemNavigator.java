package com.onyx.einfo.model;

import java.io.File;

/**
 * Created by solskjaer49 on 2017/6/8 16:50.
 * Defines the navigation actions that can be called from the note grid.
 */

public interface StorageItemNavigator {
    void onClick(StorageItemViewModel model);

    void onLongClick(StorageItemViewModel model);
}
