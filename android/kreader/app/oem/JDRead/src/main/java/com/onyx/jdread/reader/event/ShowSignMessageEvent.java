package com.onyx.jdread.reader.event;

import com.onyx.jdread.reader.common.SignNoteInfo;

/**
 * Created by huxiaomao on 2018/3/3.
 */

public class ShowSignMessageEvent {
    public SignNoteInfo signNoteInfo;

    public ShowSignMessageEvent(SignNoteInfo signNoteInfo) {
        this.signNoteInfo = signNoteInfo;
    }
}
