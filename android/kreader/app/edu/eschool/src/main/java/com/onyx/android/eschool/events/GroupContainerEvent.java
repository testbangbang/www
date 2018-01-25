package com.onyx.android.eschool.events;

import com.onyx.android.sdk.data.model.v2.GroupContainer;

/**
 * Created by suicheng on 2018/1/25.
 */
public class GroupContainerEvent {
    public GroupContainer groupContainer;

    public GroupContainerEvent(GroupContainer groupContainer) {
        this.groupContainer = groupContainer;
    }
}
