package com.onyx.kcb.event;

import com.onyx.android.sdk.data.ViewType;

/**
 * Created by suicheng on 2017/9/4.
 */

public class ViewTypeEvent {
    public ViewType viewType = ViewType.Thumbnail;

    public static ViewTypeEvent create(ViewType viewType) {
        ViewTypeEvent event = new ViewTypeEvent();
        event.viewType = viewType;
        return event;
    }
}
