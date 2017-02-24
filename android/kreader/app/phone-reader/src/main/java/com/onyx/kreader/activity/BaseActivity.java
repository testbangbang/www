package com.onyx.kreader.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.onyx.kreader.dialog.DialogProgressHolder;
import com.onyx.kreader.R;
import com.onyx.kreader.utils.ToastUtils;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by suicheng on 17/1/21.
 */
public abstract class BaseActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final int NOTHING_PERMISSIONS_REQUEST = -1000;
    private DialogProgressHolder progressDialogHolder = new DialogProgressHolder();

    protected abstract int getLayoutId();

    protected abstract void initConfig();

    protected abstract void initView();

    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        initConfig();
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        if (getPermissionRequestCode() == NOTHING_PERMISSIONS_REQUEST) {
            ToastUtils.showToast(this, "Permission RequestCode must be override", Toast.LENGTH_SHORT);
            return;
        }
        if (EasyPermissions.somePermissionPermanentlyDenied(this, list)) {
            new AppSettingsDialog.Builder(this, getPermissionRationale())
                    .setTitle(getString(R.string.goto_permission_setting))
                    .setPositiveButton(getString(R.string.go_to))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setRequestCode(getPermissionRequestCode())
                    .build()
                    .show();
        } else {
            processTemporaryPermissionsDenied(requestCode, list);
        }
    }

    protected void processTemporaryPermissionsDenied(int requestCode, List<String> list) {
        ToastUtils.showToast(this, getString(R.string.warning_of_permissions_denied), Toast.LENGTH_SHORT);
    }

    protected String getPermissionRationale() {
        return getString(R.string.tip_of_permissions_request);
    }

    //if development need request permission, must override this method
    protected int getPermissionRequestCode() {
        return NOTHING_PERMISSIONS_REQUEST;
    }

    public void showProgressDialog(final Object object, int resId) {
        if (progressDialogHolder == null) {
            return;
        }
        progressDialogHolder.showProgressDialog(this, object, resId);
    }

    public void showProgressDialog(final Object object) {
        showProgressDialog(object, R.string.loading);
    }

    public void showIndeterminateProgressDialog(final Object object, int resId) {
        if (progressDialogHolder == null) {
            return;
        }
        progressDialogHolder.showIndeterminateProgressDialog(this, object, resId);
    }

    public void showIndeterminateProgressDialog(final Object object) {
        showIndeterminateProgressDialog(object, R.string.loading);
    }

    public void setProgressDialogProgress(final Object object, int progress) {
        if (progressDialogHolder == null) {
            return;
        }
        progressDialogHolder.setProgress(object, progress);
    }

    public void dismissProgressDialog(final Object object) {
        if (progressDialogHolder != null) {
            progressDialogHolder.dismissProgressDialog(object);
        }
    }

    public void dismissAllProgressDialog() {
        if (progressDialogHolder != null) {
            progressDialogHolder.dismissAllProgressDialog();
        }
    }

    public void destroyProgressDialogHolder() {
        if (progressDialogHolder != null) {
            progressDialogHolder.dismissAllProgressDialog();
            progressDialogHolder = null;
        }
    }

    /**
     * Permission Related:
     * 1.Using EasyPermissions#hasPermissions(...) to check if the app already has the required permissions.
     * This method can take any number of permissions as its final argument.
     * 2.Requesting permissions with EasyPermissions#requestPermissions.
     * This method will request the system permissions and show the rationale string provided if necessary.
     * The request code provided should be unique to this request,
     * and the method can take any number of permissions as its final argument.
     * 3.Use rather AfterPermissionGranted annotation or override onPermissionsGranted/onPermissionsDenied to handle
     * user permission control.
     * <p>
     * example:com.onyx.android.libsetting.view.activity.WifiSettingActivity
     * ref link/more info:https://github.com/googlesamples/easypermissions/blob/master/README.md
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }
}
