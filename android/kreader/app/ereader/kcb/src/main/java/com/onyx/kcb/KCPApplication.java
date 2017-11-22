package com.onyx.kcb;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.kcb.manager.ConfigPreferenceManager;


/**
 * Created by hehai on 17-11-13.
 */

public class KCPApplication extends MultiDexApplication {

    private static KCPApplication instance = null;

    public static KCPApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(KCPApplication.this);
    }

    private void initConfig() {
        instance = this;
        DataManager.init(this, null);
        ConfigPreferenceManager.init(KCPApplication.this);
    }
}
