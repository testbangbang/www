package com.onyx.android.note;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

/**
 * Created by lxm on 2018/1/31.
 */

public class NoteApp extends MultiDexApplication {

    public static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
