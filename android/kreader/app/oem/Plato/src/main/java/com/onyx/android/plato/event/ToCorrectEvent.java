package com.onyx.android.plato.event;

import com.onyx.android.plato.cloud.bean.FinishContent;

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
