package com.onyx.android.note;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/31.
 */

public class NoteDataBundle {

    private static final NoteDataBundle ourInstance = new NoteDataBundle();

    public static NoteDataBundle getInstance() {
        return ourInstance;
    }

    private NoteDataBundle() {
    }

    private EventBus eventBus;

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }
}
