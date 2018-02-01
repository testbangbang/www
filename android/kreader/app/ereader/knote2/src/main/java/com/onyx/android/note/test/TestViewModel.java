package com.onyx.android.note.test;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.onyx.android.note.common.base.BaseViewModel;
import com.onyx.android.note.event.TestEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by lxm on 2018/1/31.
 */

public class TestViewModel extends BaseViewModel {

    public final ObservableField<String> text = new ObservableField<>();

    public TestViewModel(@NonNull EventBus eventBus) {
        super(eventBus);
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public void test() {
        getEventBus().post(new TestEvent());
    }
}
