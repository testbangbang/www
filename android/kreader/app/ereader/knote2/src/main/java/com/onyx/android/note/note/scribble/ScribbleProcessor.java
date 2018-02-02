package com.onyx.android.note.note.scribble;

import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseProcessor;
import com.onyx.android.note.note.NoteUIBundle;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/2/2.
 */

public class ScribbleProcessor extends BaseProcessor {

    private NoteUIBundle uiBundle;

    public ScribbleProcessor(@NonNull EventBus eventBus, NoteUIBundle uiBundle) {
        super(eventBus);
        this.uiBundle = uiBundle;
    }
}
