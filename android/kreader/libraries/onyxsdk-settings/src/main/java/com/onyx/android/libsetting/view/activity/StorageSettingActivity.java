package com.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import java.math.BigDecimal;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityStorageSettingBinding;
import com.onyx.android.libsetting.model.StorageInfo;
import com.onyx.android.libsetting.util.StorageSizeUtil;

public class StorageSettingActivity extends OnyxAppCompatActivity {
    ActivityStorageSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_storage_setting);
        initSupportActionBarWithCustomBackFunction();
    }

    private void updateStorageInfo() {
        float total = StorageSizeUtil.getTotalStorageAmountInGB();
        BigDecimal free = StorageSizeUtil.getFreeStorageInGB();
        float inUse = new BigDecimal(total - free.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        StorageInfo info = new StorageInfo(inUse, free.floatValue(), total);
        binding.setStorageInfo(info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStorageInfo();
    }
}
