package com.onyx.jdread;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.library.model.DataBundle;

/**
 * Created by hehai on 17-12-6.
 */

public class JDReadApplication extends MultiDexApplication {
    private static final String TAG = JDReadApplication.class.getSimpleName();
    private static JDReadApplication instance = null;
    private static DataBundle dataBundle;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(JDReadApplication.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        instance = this;
        DataManager.init(instance, null);
        PreferenceManager.init(instance);
        initEventListener();
    }

    private void initEventListener() {

    }

    public static JDReadApplication getInstance() {
        return instance;
    }

    public static DataBundle getDataBundle() {
        if (dataBundle == null) {
            dataBundle = new DataBundle(instance);
        }
        return dataBundle;
    }
}
