package com.onyx.android.libsetting.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.databinding.ActivityProductDetailSettingBinding;
import com.onyx.android.libsetting.util.BatteryUtil;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;

import java.io.File;

public class ProductDetailSettingActivity extends OnyxAppCompatActivity {
    ActivityProductDetailSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail_setting);
        initSupportActionBarWithCustomBackFunction();
        binding.buttonOpenManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = SettingConfig.sharedInstance(ProductDetailSettingActivity.this).getUserManualFile();
                if (file.exists() && file.canRead()) {
                    Intent intent = new Intent();
                    intent.setDataAndType(Uri.fromFile(file), MimeTypeUtils.mimeType(FileUtils.getFileExtension(file)));
                    intent.setAction(Intent.ACTION_VIEW);
                    ActivityUtil.startActivitySafely(ProductDetailSettingActivity.this, intent);
                }
            }
        });
        binding.batteryRemainTime.setText(BatteryUtil.getVisualBatteryRemainTime(this));
    }

}
