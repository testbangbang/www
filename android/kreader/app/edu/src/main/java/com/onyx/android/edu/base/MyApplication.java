package com.onyx.android.edu.base;

import android.app.Application;

/**
 * Created by ming on 16/6/24.
 */
public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Global.getInstance().init(getApplicationContext());
    }

}
