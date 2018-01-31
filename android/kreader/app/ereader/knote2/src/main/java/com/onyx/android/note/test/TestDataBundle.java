package com.onyx.android.note.test;

import com.onyx.android.note.common.base.BaseDataBundle;

/**
 * Created by lxm on 2018/1/31.
 */

public class TestDataBundle extends BaseDataBundle {

    private static final TestDataBundle ourInstance = new TestDataBundle();

    public static TestDataBundle getInstance() {
        return ourInstance;
    }

    private TestDataBundle() {
    }

    private TestViewModel testViewModel;

    public TestViewModel getTestViewModel() {
        if (testViewModel == null) {
            testViewModel = new TestViewModel(getEventBus());
        }
        return testViewModel;
    }
}
