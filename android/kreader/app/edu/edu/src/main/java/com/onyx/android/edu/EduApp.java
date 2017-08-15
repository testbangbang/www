package com.onyx.android.edu;

import android.app.Application;

import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;

/**
 * Created by ming on 2016/11/1.
 */

public class EduApp extends Application{

    private static EduApp instance;
    private long startTime;
    private long endTime;
    private String bookName;
    private String bookId;

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getUseTimeInSecond() {
        return (endTime - startTime) / 1000;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkDeviceConfig();
        instance = this;
    }

    public static EduApp instance() {
        return instance;
    }

    public void checkDeviceConfig() {
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }
}
