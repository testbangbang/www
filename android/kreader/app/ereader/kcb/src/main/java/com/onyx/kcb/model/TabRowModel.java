package com.onyx.kcb.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

/**
 * Created by hehai on 17-11-17.
 */

public class TabRowModel extends BaseObservable {
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> text = new ObservableField<>();
}
