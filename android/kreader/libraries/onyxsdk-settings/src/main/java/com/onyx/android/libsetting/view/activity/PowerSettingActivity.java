package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityPowerSettingBinding;
import com.onyx.android.libsetting.view.fragment.onyx.PowerSettingPreferenceFragment;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

public class PowerSettingActivity extends OnyxAppCompatActivity {
    ActivityPowerSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_power_setting);
        initSupportActionBarWithCustomBackFunction();
        getSupportFragmentManager().beginTransaction().replace(R.id.power_preference,
                new PowerSettingPreferenceFragment()).commit();
    }

}
