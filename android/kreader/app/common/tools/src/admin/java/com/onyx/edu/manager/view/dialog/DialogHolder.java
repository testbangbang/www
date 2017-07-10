package com.onyx.edu.manager.view.dialog;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

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
}
