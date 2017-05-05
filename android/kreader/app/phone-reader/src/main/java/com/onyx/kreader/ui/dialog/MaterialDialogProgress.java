package com.onyx.kreader.ui.dialog;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by suicheng on 2017/2/16.
 */
public class MaterialDialogProgress implements DialogLoading {
    MaterialDialog dialog;

    public MaterialDialogProgress(Context context, int resId, boolean indeterminate) {
        dialog = new MaterialDialog.Builder(context)
                .content(resId)
                .progress(indeterminate, 100, false)
                .build();
    }

    @Override
    public boolean isShowing() {
        return dialog.isShowing();
    }

    @Override
    public void dismiss() {
        dialog.dismiss();
    }

    @Override
    public void show() {
        dialog.show();
    }

    @Override
    public void setProgress(int progress) {
        dialog.setProgress(progress);
    }
}
