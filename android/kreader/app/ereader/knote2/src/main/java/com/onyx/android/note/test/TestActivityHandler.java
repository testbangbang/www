package com.onyx.android.note.test;

import android.support.annotation.NonNull;

import com.onyx.android.note.NoteUIBundle;
import com.onyx.android.note.common.base.BaseViewHandler;
import com.onyx.android.note.event.TestEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by lxm on 2018/1/30.
 */

public class TestActivityHandler extends BaseViewHandler {

    public TestActivityHandler(@NonNull EventBus eventBus) {
        super(eventBus);
    }

    @Subscribe
    public void onTestEvent(TestEvent event) {
        new TestAction(NoteUIBundle.getInstance().getTestViewModel()).execute(null);
    }

}
