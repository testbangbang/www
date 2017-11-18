package com.onyx.kcb.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

/**
 * Created by hehai on 17-11-16.
 */

public class DialogAddEditModel extends BaseObservable {
    public final ObservableList<String> titles = new ObservableArrayList<>();
    public final ObservableList<TabRowModel> edits = new ObservableArrayList<>();
    public final ObservableField<String> contentTitle = new ObservableField<>();
    public final ObservableField<String> combine = new ObservableField<>();
    public final ObservableField<String> spitSymbol = new ObservableField<>();

    public int getEditCount() {
        return titles.size();
    }
}
