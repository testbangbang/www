package com.onyx.android.eschool.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.request.cloud.LogCollectionRequest;
import com.onyx.android.sdk.ui.dialog.OnyxAlertDialog;
import com.onyx.android.sdk.ui.wifi.NetworkHelper;
import com.onyx.android.sdk.utils.InputMethodUtils;
import com.onyx.android.sdk.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by suicheng on 2017/3/27.
 */

public class ErrorReportActivity extends BaseActivity {
    private static final String TAG = ErrorReportActivity.class.getSimpleName();
    private static final int FINISH_ACTIVITY_DELAY = 1000;

    @Bind(R.id.editText_description)
    EditText descEdit;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_error_report;
    }

    @Override
    protected void initConfig() {
        NetworkHelper.enableWifi(this, true);
    }

    @Override
    protected void initView() {
        initSupportActionBarWithCustomBackFunction();
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
    protected void initData() {
    }

    @Override
    public void onBackPressed() {
        if (StringUtils.isNotBlank(getEditDescText())) {
            showQuitEditDialog();
            return;
        }
        super.onBackPressed();
    }

    @OnClick(R.id.button_positive)
    void onPositiveClick() {
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
        final LogCollectionRequest collectionRequest = new LogCollectionRequest(SchoolApp.getLogOssManger(this), desc);
        SchoolApp.getCloudStore().submitRequest(this, collectionRequest, new BaseCallback() {
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
