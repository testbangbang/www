package com.onyx.kreader.dialog;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 12/8/14.
 */
public class DialogProgressHolder {
    private static final String TAG = DialogProgressHolder.class.getSimpleName();

    private Map<Object, DialogLoading> dialogLoadingMap = new HashMap<>();

    public DialogLoading getProgressDialogFromObject(final Object object) {
        return dialogLoadingMap.get(object);
    }

    public DialogLoading getProgressDialog(final Context context, final Object object, int resId, boolean indeterminate) {
        DialogLoading dialogLoading = getProgressDialogFromObject(object);
        if (dialogLoading == null) {
            dialogLoading = new MaterialDialogProgress(context, resId, indeterminate);
            dialogLoadingMap.put(object, dialogLoading);
        }
        return dialogLoading;
    }

    public DialogLoading showProgressDialog(final Context context, final Object object, int resId) {
        DialogLoading dialogLoading = getProgressDialog(context, object, resId, false);
        dialogLoading.show();
        return dialogLoading;
    }

    public DialogLoading showIndeterminateProgressDialog(final Context context, final Object object, int resId) {
        DialogLoading dialogLoading = getProgressDialog(context, object, resId, true);
        dialogLoading.show();
        return dialogLoading;
    }

    public void setProgress(final Object request, final int progress) {
        DialogLoading dialogLoading = getProgressDialogFromObject(request);
        if (dialogLoading != null) {
            dialogLoading.setProgress(progress);
        }
    }

    public void dismissProgressDialog(final Object object) {
        DialogLoading dialogLoading = getProgressDialogFromObject(object);
        if (dialogLoading != null && dialogLoading.isShowing()) {
            try {
                dialogLoading.dismiss();
            } catch (Throwable tr) {
                Log.w(TAG, tr);
            }
            dialogLoadingMap.remove(object);
        }
    }

    public void dismissAllProgressDialog() {
        for (Map.Entry<Object, DialogLoading> entry : dialogLoadingMap.entrySet()) {
            entry.getValue().dismiss();
        }
        dialogLoadingMap.clear();
    }
}
