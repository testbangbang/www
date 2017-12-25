package com.onyx.jdread.shop.event;


import com.onyx.jdread.shop.model.ProgressInfoModel;

/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadingEvent {
    public Object tag;
    public ProgressInfoModel progressInfoModel;

    public DownloadingEvent() {
    }

    public DownloadingEvent(Object tag,ProgressInfoModel progressInfoModel) {
        this.tag = tag;
        this.progressInfoModel = progressInfoModel;
    }
}
