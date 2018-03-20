package com.onyx.android.update;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.update.dialog.DialogMessage;
import com.onyx.android.update.upgrade.RxFirmwareLocalUpdateRequest;

/**
 * Created by suicheng on 2018/3/20.
 */
public class SystemUpgradeActivity extends AppCompatActivity {
    public static final String SYSTEM_UPGRADE_PATH = "system_upgrade_path";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_upgrade);
        initData();
    }

    private void initData() {
        String path = getIntent().getStringExtra(SYSTEM_UPGRADE_PATH);
        Log.i(getClass().getSimpleName(), String.valueOf(path));
        if (StringUtils.isNullOrEmpty(path)) {
            finish();
            return;
        }
        startSystemUpgrade(path);
    }

    private void startSystemUpgrade(final String path) {
        RxFirmwareLocalUpdateRequest.setAppContext(getApplicationContext());
        final RxFirmwareLocalUpdateRequest upgradeRequest = new RxFirmwareLocalUpdateRequest(path);
        upgradeRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
            }

            @Override
            public void onFinally() {
                if (!upgradeRequest.isSuccess()) {
                    showUpgradeFailedDialog(path);
                }
            }
        });
    }

    private void showUpgradeFailedDialog(final String filePath) {
        final DialogMessage dialog = new DialogMessage(this);
        dialog.setMessage(getString(R.string.system_upgrade_fail_message));
        dialog.setPositiveAction(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteUpgradeFile(filePath);
                finish();
            }
        });
        dialog.show();
    }

    private void deleteUpgradeFile(String path) {
        if (StringUtils.isNotBlank(path)) {
            FileUtils.deleteFile(path);
        }
    }
}
