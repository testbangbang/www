package com.onyx.android.eschool.activity;

import android.content.ComponentName;
import android.content.Intent;

import com.onyx.android.eschool.R;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

import java.io.File;

import butterknife.OnClick;

/**
 * Created by suicheng on 2017/6/3.
 */
public class DeviceParamsActivity extends BaseActivity {

    private String userManualFilePath = "/mnt/sdcard/user_manual.pdf";

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_device_params;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
    }

    @OnClick(R.id.manual_guide_bt)
    void onManualGuideClick() {
        ActivityUtil.startActivitySafely(this, getUserManualIntent());
    }

    private Intent getUserManualIntent() {
        File file = new File(userManualFilePath);
        if (!file.exists()) {
            ToastUtils.showToast(this, "用户手册文档不存在!");
            return null;
        }
        Intent intent = ViewDocumentUtils.viewActionIntentWithMimeType(new File(userManualFilePath));
        ComponentName component = ViewDocumentUtils.getReaderComponentName(this);
        if (component != null) {
            intent.setComponent(component);
        }
        return intent;
    }
}
