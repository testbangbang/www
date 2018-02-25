package com.onyx.android.note.handler;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.note.NoteManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/2/5.
 */

public class SelectionHandler extends BaseHandler {

    public SelectionHandler(@NonNull EventBus eventBus, NoteManager noteManager) {
        super(eventBus, noteManager);
    }
}
