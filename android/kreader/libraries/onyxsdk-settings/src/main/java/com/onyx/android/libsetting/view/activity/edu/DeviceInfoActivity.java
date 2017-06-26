package com.onyx.android.libsetting.view.activity.edu;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityDeviceInfoBinding;
import com.onyx.android.libsetting.view.fragment.edu.DeviceInfoFragment;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

public class DeviceInfoActivity extends OnyxAppCompatActivity {
    ActivityDeviceInfoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_device_info);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.device_info_preference, new DeviceInfoFragment()).commit();
    }

}
