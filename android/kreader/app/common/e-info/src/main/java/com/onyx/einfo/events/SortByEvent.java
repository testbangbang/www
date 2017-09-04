package com.onyx.einfo.events;

import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;

/**
 * Created by suicheng on 2017/9/4.
 */

public class SortByEvent {
    public SortBy sortBy;
    public SortOrder sortOrder;

    public static SortByEvent create(SortBy sortBy, SortOrder sortOrder) {
        SortByEvent event = new SortByEvent();
        event.sortBy = sortBy;
        event.sortOrder = sortOrder;
        return event;
    }
}
