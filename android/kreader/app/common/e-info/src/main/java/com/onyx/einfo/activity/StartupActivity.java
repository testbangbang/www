package com.onyx.einfo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.einfo.device.DeviceConfig;

/**
 * Created by suicheng on 2017/7/28.
 */
public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startInitialActivityByConfig();
    }

    private void startInitialActivityByConfig() {
        DeviceConfig deviceConfig = DeviceConfig.sharedInstance(this);
        startHomeActivityByDeviceConfig(deviceConfig);
    }

    private boolean startHomeActivityByDeviceConfig(DeviceConfig deviceConfig) {
        String packageName = deviceConfig.getHomeActivityPackageName();
        String className = deviceConfig.getHomeActivityClassName();
        Intent intent = getDefaultHomeIntent();
        if (StringUtils.isNotBlank(packageName) && StringUtils.isNotBlank(className)) {
            intent = ActivityUtil.createIntent(packageName, className);
        }
        if (ActivityUtil.startActivitySafely(this, intent)) {
            finish();
            return true;
        }
        return false;
    }

    private Intent getDefaultHomeIntent() {
        return new Intent(this, HomeActivity.class);
    }
}
