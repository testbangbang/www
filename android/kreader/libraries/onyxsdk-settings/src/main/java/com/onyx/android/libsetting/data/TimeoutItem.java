package com.onyx.android.libsetting.data;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by solskjaer49 on 2017/5/12 15:04.
 */

public class TimeoutItem extends BaseObservable {
    private int timeoutEntryValue;
    private String timeoutEntry;
    private boolean isChecked;

    @Bindable
    public int getTimeoutEntryValue() {
        return timeoutEntryValue;
    }

    public void setTimeoutEntryValue(int timeoutEntryValue) {
        this.timeoutEntryValue = timeoutEntryValue;
    }

    @Bindable
    public String getTimeoutEntry() {
        return timeoutEntry;
    }

    public void setTimeoutEntry(String timeoutEntry) {
        this.timeoutEntry = timeoutEntry;
    }

    @Bindable
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
