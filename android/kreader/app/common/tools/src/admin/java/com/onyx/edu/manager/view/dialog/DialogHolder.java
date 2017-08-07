package com.onyx.edu.manager.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.R;

/**
 * Created by suicheng on 2017/7/6.
 */
public class DialogHolder {

    public static MaterialDialog showProgressDialog(Context context, String content) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .content(content)
                .progress(true, 100, false)
                .build();
        dialog.show();
        return dialog;
    }

    public static MaterialDialog showDownloadingDialog(Context context, String content) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .content(content)
                .progress(false, 100, false)
                .canceledOnTouchOutside(false)
                .build();
        dialog.show();
        return dialog;
    }

    public static MaterialDialog showAlertDialog(Context context, String title, String content,
                                                 MaterialDialog.SingleButtonCallback onPositiveCallback) {
        MaterialDialog.Builder builder = getDialogBaseBuilder(context, title, onPositiveCallback);
        if (StringUtils.isNotBlank(content)) {
            builder.content(content);
        }
        return builder.show();
    }

    public static MaterialDialog.Builder getDialogBaseBuilder(Context context, String title,
                                                              MaterialDialog.SingleButtonCallback onPositiveCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .contentGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.colorPrimary)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.darker_gray)
                .onPositive(onPositiveCallback);
        if (StringUtils.isNotBlank(title)) {
            builder.title(title);
        }
        return builder;
    }
}
