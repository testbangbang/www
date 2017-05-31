package com.onyx.android.eschool.activity;

import android.os.Bundle;
import android.app.Activity;

import com.onyx.android.eschool.R;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.utils.DeviceReceiver;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.umeng.analytics.MobclickAgent;

public class RouterTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_test);
        DeviceUtils.turnOffSystemPMSettings(this);

        DeviceReceiver receiver = new DeviceReceiver();
        receiver.setWifiStateListener(new DeviceReceiver.WifiStateListener() {

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initUMeng() {
        MobclickAgent.setDebugMode(true);
        MobclickAgent.setCheckDevice(true);
        MobclickAgent.setLatencyWindow(0);
    }


}
