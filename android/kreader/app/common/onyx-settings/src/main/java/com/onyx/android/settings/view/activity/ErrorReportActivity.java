package com.onyx.android.settings.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.request.cloud.LogCollectionRequest;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.settings.R;
import com.onyx.android.settings.SettingsApplication;

/**
 * Created by suicheng on 2017/3/27.
 */

public class ErrorReportActivity extends OnyxAppCompatActivity {
    private static final String TAG = ErrorReportActivity.class.getSimpleName();
    private static final int FINISH_ACTIVITY_DELAY = 1000;

    private EditText descEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_report);

        initConfig();
        initView();
    }

    private void initConfig() {
        NetworkHelper.enableWifi(this, true);
    }

    private void initView() {
        initSupportActionBarWithCustomBackFunction();
        descEdit = (EditText) findViewById(R.id.editText_description);
        findViewById(R.id.button_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPositiveClick();
            }
        });
    }

    private String getEditDescText() {
        return descEdit.getEditableText().toString().trim();
    }

    private void showQuitEditDialog() {
        OnyxAlertDialog dialog = new OnyxAlertDialog();
        dialog.setParams(new OnyxAlertDialog.Params()
                .setEnableTittle(false)
                .setAlertMsgString(getString(R.string.wanna_give_up_edit))
                .setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }));
        dialog.show(getFragmentManager(), TAG);
    }

    @Override
    public void onBackPressed() {
        if (StringUtils.isNotBlank(getEditDescText())) {
            showQuitEditDialog();
            return;
        }
        super.onBackPressed();
    }

    private void onPositiveClick() {
        if (!NetworkHelper.requestWifi(this)) {
            return;
        }
        String desc = getEditDescText();
        if (StringUtils.isNullOrEmpty(desc)) {
            showToast(R.string.desc_can_not_empty, Toast.LENGTH_SHORT);
            return;
        }
        InputMethodUtils.hideInputKeyboard(this);
        startLogFeedback(desc);
    }

    private void startLogFeedback(String desc) {
        showToast(R.string.feedback_submitting, Toast.LENGTH_SHORT);
        final LogCollectionRequest collectionRequest = new LogCollectionRequest(SettingsApplication.getLogOssManger(this), desc);
        OTAManager.sharedInstance().submitRequest(this, collectionRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                boolean success = true;
                if (e != null || StringUtils.isNullOrEmpty(collectionRequest.getUploadFileUrl())) {
                    success = false;
                    printException(e);
                }
                showToast(success ? R.string.feedback_success : R.string.feedback_fail_and_network_check, Toast.LENGTH_SHORT);
                if (success) {
                    finishActivityDelay();
                }
            }
        });
    }

    private void printException(Throwable e) {
        if (e != null) {
            e.printStackTrace();
        }
    }

    private void finishActivityDelay() {
        descEdit.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, FINISH_ACTIVITY_DELAY);
    }
}
