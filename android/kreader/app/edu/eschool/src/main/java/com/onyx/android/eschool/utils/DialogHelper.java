package com.onyx.android.eschool.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.onyx.android.eschool.R;

/**
 * Created by suicheng on 2018/1/12.
 */

public class DialogHelper {

    @NonNull
    public static AlertDialog getAlertDialog(final Context context, String title, String message,
                                             String positiveText, DialogInterface.OnClickListener positiveAction) {
        if (positiveAction == null) {
            positiveAction = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(title)
                .setIcon(R.drawable.info_icon)
                .setMessage(message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(positiveText, positiveAction);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BroadcastHelper.sendDialogOpenBroadcast(context, "");
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                BroadcastHelper.sendDialogCloseBroadcast(context, "");
            }
        });
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return dialog;
    }
}
