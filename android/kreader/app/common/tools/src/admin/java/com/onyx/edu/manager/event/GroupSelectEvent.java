package com.onyx.edu.manager.event;

import com.onyx.android.sdk.data.model.v2.CloudGroup;

/**
 * Created by suicheng on 2017/7/8.
 */

public class GroupSelectEvent {
    public CloudGroup group;

    public GroupSelectEvent(CloudGroup group) {
        this.group = group;
    }
}
