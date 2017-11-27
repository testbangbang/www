package com.onyx.android.plato.event;

import com.onyx.android.plato.cloud.bean.ContentBean;

/**
 * Created by li on 2017/10/16.
 */

public class ToCorrectEvent {
    private ContentBean content;

    public ToCorrectEvent(ContentBean content) {
        this.content = content;
    }

    public ContentBean getContent() {
        return content;
    }
}
