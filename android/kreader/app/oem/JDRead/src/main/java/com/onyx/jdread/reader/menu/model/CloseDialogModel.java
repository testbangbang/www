package com.onyx.jdread.reader.menu.model;

import com.onyx.jdread.reader.dialog.ViewCallBack;
import com.onyx.jdread.reader.event.CloseDocumentEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/2/3.
 */

public class CloseDialogModel {
    private EventBus eventBus;
    private ViewCallBack callBack;

    public CloseDialogModel(EventBus eventBus, ViewCallBack callBack) {
        this.eventBus = eventBus;
        this.callBack = callBack;
    }

    public void backClick() {
        dismissZoneClick();
        eventBus.post(new CloseDocumentEvent());
    }

    public void dismissZoneClick() {
        if(callBack != null){
            callBack.getContent().dismiss();
        }
    }
}
