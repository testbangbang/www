package com.onyx.edu.manager.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.common.ContentException;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.request.cloud.v2.CloudUserInfoByMacRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.qrcode.QrCodeActivity;
import com.onyx.android.sdk.ui.utils.ToastUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.manager.AdminApplication;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.event.DeviceBindCommitEvent;
import com.onyx.edu.manager.event.DeviceUserInfoSwitchEvent;
import com.onyx.edu.manager.event.GroupReSelectEvent;
import com.onyx.edu.manager.event.GroupSelectEvent;
import com.onyx.edu.manager.view.dialog.DialogHolder;
import com.onyx.edu.manager.view.fragment.DeviceBindCommitFragment;
import com.onyx.edu.manager.view.fragment.DeviceUserInfoFragment;
import com.onyx.edu.manager.view.fragment.GroupListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/7/7.
 */
public class QrScannerActivity extends AppCompatActivity {
    private static final int REQUEST_QR_CODE = 1000;

    public static CloudGroup groupSelected;
    private DeviceBind scannedDeviceBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        ButterKnife.bind(this);
        startQrScanner();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.id_content, fragment);
        transaction.commit();
    }

    private void startQrScanner() {
        startActivityForResult(new Intent(this, QrCodeActivity.class), REQUEST_QR_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        switch (requestCode) {
            case REQUEST_QR_CODE:
                processRequestQrCode(intent);
                break;
        }
    }

    private void processRequestQrCode(Intent intent) {
        if (intent == null) {
            return;
        }
        String qrCode = intent.getStringExtra("qrCode");
        scannedDeviceBind = JSONObjectParseUtils.parseObject(qrCode, DeviceBind.class);
        if (scannedDeviceBind == null || !NetworkUtil.isStringValidMacAddress(scannedDeviceBind.mac)) {
            ToastUtils.showToast(getApplicationContext(), R.string.qr_code_not_match);
            finish();
            return;
        }
        fetchUserInfoFromCloud(scannedDeviceBind);
    }

    private void fetchUserInfoFromCloud(final DeviceBind deviceBind) {
        final MaterialDialog dialog = DialogHolder.showProgressDialog(this, getString(R.string.qr_code_querying));
        final CloudUserInfoByMacRequest userInfoByMacRequest = new CloudUserInfoByMacRequest(deviceBind.mac);
        AdminApplication.getCloudManager().submitRequest(this, userInfoByMacRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialog.dismiss();
                if (e != null) {
                    if (ContentException.isCloudException(e)) {
                        processCloudException((ContentException) e, deviceBind);
                        return;
                    }
                    processNetWorkException();
                    return;
                }
                processBoundUserInfo(userInfoByMacRequest.getGroupUserInfo());
            }
        });
    }

    private void processNetWorkException() {
        ToastUtils.showToast(getApplicationContext(), R.string.qr_code_query_exception);
        finish();
    }

    private void processCloudException(ContentException cloudException, DeviceBind deviceBind) {
        if (cloudException.isCloudNotFound()) {
            processUnboundUserInfo(deviceBind);
            return;
        }
        ToastUtils.showToast(getApplicationContext(), String.valueOf(cloudException.getMessage()));
        finish();
    }

    private void processBoundUserInfo(GroupUserInfo groupUserInfo) {
        showFragment(DeviceBindCommitFragment.newInstance(groupUserInfo, false));
    }

    private void processUnboundUserInfo(final DeviceBind deviceBind) {
        if (groupSelected != null) {
            GroupUserInfo groupUserInfo = new GroupUserInfo();
            groupUserInfo.groups = new ArrayList<>();
            groupUserInfo.groups.add(groupSelected);
            groupUserInfo.device = deviceBind;
            showFragment(DeviceUserInfoFragment.newInstance(groupUserInfo));
        } else {
            showFragment(GroupListFragment.newInstance());
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceBindCommitEvent(DeviceBindCommitEvent event) {
        event.groupUserInfo.device = scannedDeviceBind;
        showFragment(DeviceBindCommitFragment.newInstance(event.groupUserInfo, true));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceUserInfoSwitchEvent(DeviceUserInfoSwitchEvent event) {
        processUnboundUserInfo(scannedDeviceBind);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupSelectEvent(GroupSelectEvent event) {
        groupSelected = event.group;
        processUnboundUserInfo(scannedDeviceBind);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupReSelectEvent(GroupReSelectEvent event) {
        showFragment(GroupListFragment.newInstance());
    }
}
