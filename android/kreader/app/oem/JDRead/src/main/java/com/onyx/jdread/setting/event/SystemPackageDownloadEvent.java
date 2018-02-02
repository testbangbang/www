package com.onyx.jdread.setting.event;

/**
 * Created by li on 2018/2/2.
 */

public class SystemPackageDownloadEvent {
    private int progress;

    public SystemPackageDownloadEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
