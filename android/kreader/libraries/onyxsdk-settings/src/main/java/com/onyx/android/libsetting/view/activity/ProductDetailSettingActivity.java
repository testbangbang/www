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
import com.onyx.android.sdk.utils.ActivityUtil;

import java.io.File;

public class ProductDetailSettingActivity extends Activity {
    static public final String DEFAULT_USER_RESOURCES_CONFIG_PATH = "/system/user-res";
    static public final String DEFAULT_USER_MANUAL_NAME = "user-manual.pdf";
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
                File file = new File(DEFAULT_USER_RESOURCES_CONFIG_PATH + File.separator + DEFAULT_USER_MANUAL_NAME);
                if (file.exists() && file.canRead()) {
                    Intent intent = new Intent();
                    intent.setData(Uri.fromFile(file));
                    intent.setAction(Intent.ACTION_VIEW);
                    ActivityUtil.startActivitySafely(ProductDetailSettingActivity.this, intent);
                }
            }
        });
    }

}
