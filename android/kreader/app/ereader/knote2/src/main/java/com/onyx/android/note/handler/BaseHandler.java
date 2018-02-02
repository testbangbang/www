package com.onyx.android.note.handler;

import android.support.annotation.NonNull;

import com.onyx.android.note.note.NoteUIBundle;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/2/2.
 */

public class BaseHandler {

    private EventBus eventBus;
    private NoteUIBundle uiBundle;

    public BaseHandler(@NonNull EventBus eventBus, @NonNull NoteUIBundle uiBundle) {
        this.eventBus = eventBus;
        this.uiBundle = uiBundle;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public NoteUIBundle getUiBundle() {
        return uiBundle;
    }
}
