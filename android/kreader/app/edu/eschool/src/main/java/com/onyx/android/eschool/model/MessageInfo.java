package com.onyx.android.eschool.model;

import com.onyx.android.sdk.im.data.Message;

/**
 * Created by suicheng on 2018/1/19.
 */

public class MessageInfo {
    public String title;
    public String info;
    public Message message;

    public static MessageInfo create(String title, String info, Message message) {
        MessageInfo simpleInfo = new MessageInfo();
        simpleInfo.title = title;
        simpleInfo.info = info;
        simpleInfo.message = message;
        return simpleInfo;
    }
}
