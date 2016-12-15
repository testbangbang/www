package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityDateTimeSettingBinding;

public class DateTimeSettingActivity extends OnyxAppCompatActivity {
    ActivityDateTimeSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_date_time_setting);
        initSupportActionBarWithCustomBackFunction();
    }





}
