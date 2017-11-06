package com.onyx.android.sun.event;

import com.onyx.android.sun.cloud.bean.FinishContent;

/**
 * Created by li on 2017/10/16.
 */

public class ToCorrectEvent {
    private FinishContent content;

    public ToCorrectEvent(FinishContent content) {
        this.content = content;
    }

    public FinishContent getContent() {
        return content;
    }
}
