package com.onyx.kreader.manager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.onyx.kreader.R;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by suicheng on 2017/2/24.
 */
public class PermissionManager {

    public static void processPermissionPermanentlyDenied(Fragment fragment, String rationale, int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(fragment, perms)) {
            showDialog(fragment.getContext().getApplicationContext(), requestCode, getDialogBuilder(fragment, rationale));
        } else {
            showWarningPermissionsDeniedToast(fragment.getContext().getApplicationContext());
        }
    }

    public static void processPermissionPermanentlyDenied(Activity activity, String rationale, int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
            showDialog(activity.getApplicationContext(), requestCode, getDialogBuilder(activity, rationale));
        } else {
            showWarningPermissionsDeniedToast(activity.getApplicationContext());
        }
    }

    private static void showWarningPermissionsDeniedToast(Context context) {
        Toast.makeText(context, R.string.warning_of_permissions_denied, Toast.LENGTH_SHORT).show();
    }

    private static void showDialog(Context context, int requestCode, AppSettingsDialog.Builder builder) {
        builder.setTitle(context.getString(R.string.goto_permission_setting))
                .setPositiveButton(context.getString(R.string.go_to))
                .setNegativeButton(context.getString(R.string.cancel), null)
                .setRequestCode(requestCode)
                .build()
                .show();
    }

    private static AppSettingsDialog.Builder getDialogBuilder(Fragment fragment, String rationale) {
        return new AppSettingsDialog.Builder(fragment, rationale);
    }

    private static AppSettingsDialog.Builder getDialogBuilder(Activity activity, String rationale) {
        return new AppSettingsDialog.Builder(activity, rationale);
    }
}
