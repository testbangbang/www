package com.onyx.edu.manager.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.manager.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suicheng on 2017/7/6.
 */
public class DialogHolder {
    private Handler progressHandler = new Handler();
    private Map<Dialog, DialogRunnable> dialogRunnableMap = new HashMap<>();

    public static MaterialDialog getProgressDialog(Context context) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .canceledOnTouchOutside(false)
                .progress(true, 100, false)
                .build();
        dialog.setContent(R.string.loading);
        return dialog;
    }

    public static MaterialDialog showProgressDialog(Context context, String content) {
        MaterialDialog dialog = getProgressDialog(context);
        dialog.setContent(content);
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

    public static class DialogRunnable implements Runnable {
        private Dialog dialog;
        private boolean isDismiss = false;

        public DialogRunnable(final Dialog dialog) {
            this.dialog = dialog;
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isDismiss = true;
                }
            });
        }

        @Override
        public void run() {
            if (dialog != null && !isDismiss) {
                dialog.show();
            }
        }
    }

    public void postShowDialog(final MaterialDialog dialog) {
        DialogHolder.DialogRunnable runnable = new DialogHolder.DialogRunnable(dialog);
        dialogRunnableMap.put(dialog, runnable);
        progressHandler.postDelayed(runnable, 350);
    }

    public void showDialog(final MaterialDialog dialog) {
        DialogHolder.DialogRunnable runnable = new DialogHolder.DialogRunnable(dialog);
        dialogRunnableMap.put(dialog, runnable);
        progressHandler.post(runnable);
    }

    public void dismissDialog(final MaterialDialog dialog) {
        DialogHolder.DialogRunnable runnable = dialogRunnableMap.get(dialog);
        if (runnable != null) {
            progressHandler.removeCallbacks(runnable);
        }
        if (!dialog.isShowing()) {
            dialog.hide();
        }
        dialog.dismiss();
    }
}
