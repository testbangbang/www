package com.onyx.android.note;

import com.onyx.android.note.test.TestViewModel;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/31.
 */

public class NoteUIBundle {
    private static final NoteUIBundle ourInstance = new NoteUIBundle();

    public static NoteUIBundle getInstance() {
        return ourInstance;
    }

    private NoteUIBundle() {
    }

    private TestViewModel testViewModel;

    private EventBus getEventBus() {
        return NoteDataBundle.getInstance().getEventBus();
    }

    public TestViewModel getTestViewModel() {
        if (testViewModel == null) {
            testViewModel = new TestViewModel(getEventBus());
        }
        return testViewModel;
    }
}
