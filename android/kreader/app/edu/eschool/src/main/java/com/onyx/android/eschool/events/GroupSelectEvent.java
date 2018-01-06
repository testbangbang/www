package com.onyx.android.eschool.events;

import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;

/**
 * Created by suicheng on 2017/12/21.
 */
public class GroupSelectEvent {
    public int index;
    public CloudGroup group;

    public GroupSelectEvent(CloudGroup group, int index) {
        this.index = index;
        this.group = group;
    }
}
