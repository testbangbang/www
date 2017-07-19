package com.onyx.android.dr.reader.utils;

import android.os.FileObserver;

import com.onyx.android.dr.reader.common.ReaderConstants;
import com.onyx.android.dr.reader.event.NewFileCreatedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-6-19.
 */

public class CustomFileObserver extends FileObserver {
    private String path;
    public CustomFileObserver(String path) {
        super(path);
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public void onEvent(int event, String path) {
        switch (event) {
            case ReaderConstants.CREATED_DIRECTORY:
            case FileObserver.CREATE:
                EventBus.getDefault().post(new NewFileCreatedEvent());
                break;
        }
    }

    @Override
    public void startWatching() {
        super.startWatching();
    }
}
