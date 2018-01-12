package com.onyx.jdread.personal.event;

import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;

/**
 * Created by li on 2018/1/12.
 */

public class UserInfoEvent {
    private UserInfo userInfo;

    public UserInfoEvent(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }
}
