package com.onyx.android.libsetting.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.onyx.android.libsetting.R;
import com.onyx.android.libsetting.databinding.ActivityProductDetailSettingBinding;
import com.onyx.android.libsetting.util.BatteryUtil;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.MimeTypeUtils;

import java.io.File;

public class ProductDetailSettingActivity extends Activity {
    static public final String DEFAULT_USER_RESOURCES_CONFIG_PATH = Device.currentDevice.getExternalStorageDirectory().getPath() + File.separator + "PL107_user_manual";
    //TODO:avoid hard code here?
    static public final String DEFAULT_USER_MANUAL_NAME = "YOUNGY BOOX用户手册.pdf";
    ActivityProductDetailSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail_setting);
        binding.buttonOpenManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(DEFAULT_USER_RESOURCES_CONFIG_PATH +
                        File.separator + DEFAULT_USER_MANUAL_NAME);
                if (file.exists() && file.canRead()) {
                    Intent intent = new Intent();
                    intent.setDataAndType(Uri.fromFile(file),MimeTypeUtils.mimeType(FileUtils.getFileExtension(file)));
                    intent.setAction(Intent.ACTION_VIEW);
                    ActivityUtil.startActivitySafely(ProductDetailSettingActivity.this, intent);
                }
            }
        });
        binding.batteryRemainTime.setText(BatteryUtil.getVisualBatteryRemainTime(this));
    }

}
