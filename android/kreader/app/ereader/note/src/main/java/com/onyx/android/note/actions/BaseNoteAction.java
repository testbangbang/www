package com.onyx.android.note.actions;

import android.app.Activity;
import android.os.Bundle;

import com.onyx.android.note.dialog.DialogLoading;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by zhuzeng on 6/26/16.
 */
public abstract class BaseNoteAction<T extends Activity> {
    private DialogLoading loadingDialog;

    public void showLoadingDialog(final T activity, final String key, final int stringResId) {
        loadingDialog = new DialogLoading();
        Bundle args = new Bundle();
        args.putString(key, activity.getApplicationContext().getString(stringResId));
        loadingDialog.setArguments(args);
        loadingDialog.show(activity.getFragmentManager());
    }

    public void dismissLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public abstract void execute(final T activity,  final BaseCallback callback);



}
