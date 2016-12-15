package com.onyx.android.libsetting.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.onyx.android.libsetting.BR;

/**
 * Created by solskjaer49 on 2016/12/3 18:25.
 */

public class StorageInfo extends BaseObservable {
    private float spaceInUse;
    private float freeSpace;
    private float totalSpace;

    public StorageInfo(float spaceInUse, float freeSpace, float totalSpace) {
        this.spaceInUse = spaceInUse;
        this.freeSpace = freeSpace;
        this.totalSpace = totalSpace;
    }

    @Bindable
    public float getSpaceInUse() {
        return spaceInUse;
    }

    public StorageInfo setSpaceInUse(float spaceInUse) {
        this.spaceInUse = spaceInUse;
        notifyPropertyChanged(BR.spaceInUse);
        return this;
    }

    @Bindable
    public float getFreeSpace() {
        return freeSpace;
    }

    public StorageInfo setFreeSpace(float freeSpace) {
        this.freeSpace = freeSpace;
        notifyPropertyChanged(BR.freeSpace);
        return this;
    }

    @Bindable
    public float getTotalSpace() {
        return totalSpace;
    }

    public StorageInfo setTotalSpace(float totalSpace) {
        this.totalSpace = totalSpace;
        notifyPropertyChanged(BR.totalSpace);
        return this;
    }
}
