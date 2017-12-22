package com.onyx.jdread.shop.event;

import com.onyx.android.sdk.common.request.BaseCallback.ProgressInfo;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadingEvent {
    public Object tag;
    public ProgressInfo progressInfo;

    public DownloadingEvent() {
    }

    public DownloadingEvent(Object tag,ProgressInfo progressInfo) {
        this.tag = tag;
        this.progressInfo = progressInfo;
    }
}
