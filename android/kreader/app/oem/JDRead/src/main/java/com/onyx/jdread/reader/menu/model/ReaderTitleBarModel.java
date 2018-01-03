package com.onyx.jdread.reader.menu.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-12-27.
 */

public class ReaderTitleBarModel extends BaseObservable {
    public final ObservableField<String> title = new ObservableField<>();

    public ReaderTitleBarModel() {

    }

    public void back() {
    }
}
