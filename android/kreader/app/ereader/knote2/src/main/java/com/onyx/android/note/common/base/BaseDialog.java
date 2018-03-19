package com.onyx.android.note.common.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.common.eventbus.EventBus;
import com.onyx.android.note.event.DialogChangeEvent;

/**
 * Created by lxm on 2018/1/30.
 */

public class BaseDialog extends Dialog {

    private EventBus eventBus;

    public BaseDialog(@NonNull Context context, EventBus eventBus) {
        super(context);
        this.eventBus = eventBus;
    }

    @Override
    public void show() {
        eventBus.post(new DialogChangeEvent(true));
        super.show();
    }

    @Override
    public void dismiss() {
        eventBus.post(new DialogChangeEvent(false));
        super.dismiss();
    }

}
