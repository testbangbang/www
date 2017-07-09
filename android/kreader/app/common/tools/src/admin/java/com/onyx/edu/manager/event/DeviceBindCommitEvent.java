package com.onyx.edu.manager.event;

import com.onyx.android.sdk.data.model.v2.GroupUserInfo;

/**
 * Created by suicheng on 2017/7/7.
 */
public class DeviceBindCommitEvent {
    public GroupUserInfo groupUserInfo;

    public DeviceBindCommitEvent(GroupUserInfo groupUserInfo) {
        this.groupUserInfo = groupUserInfo;
    }
}
