package com.onyx.android.sdk.ui.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuzeng on 12/8/14.
 */
public class DialogProgressHolder {
    private static final String TAG = DialogProgressHolder.class.getSimpleName();

    private Map<Object, DialogLoading> dialogLoadingMap = new HashMap<>();
    private Map<Object, DialogCancelListener> cancelListenerMap = new HashMap<>();

    public interface DialogCancelListener {
        void onCancel();
    }

    public DialogLoading getProgressDialogFromRequest(final Object object) {
        return dialogLoadingMap.get(object);
    }

    public DialogLoading getProgressDialog(final Context context, final Object object, int resId, DialogCancelListener listener) {
        return getProgressDialog(context, object, context.getString(resId), listener);
    }

    public DialogLoading getProgressDialog(final Context context, final Object object, String msg, DialogCancelListener listener) {
        DialogLoading dialogLoading = getProgressDialogFromRequest(object);
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(context, msg, true);
            dialogLoading.setCancelButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissProgressDialog(object);
                    processCancelListener(object);
                }
            });
            dialogLoadingMap.put(object, dialogLoading);
            cancelListenerMap.put(object, listener);
        }
        return dialogLoading;
    }

    public DialogLoading showProgressDialog(final Context context, final Object object, String msg, DialogCancelListener listener) {
        DialogLoading dialogLoading = getProgressDialog(context, object, msg, listener);
        dialogLoading.show();
        return dialogLoading;
    }

    public DialogLoading showProgressDialog(final Context context, final Object object, int resId, DialogCancelListener listener) {
        return showProgressDialog(context, object, context.getString(resId), listener);
    }

    public void setMessage(final Object request, final String text) {
        DialogLoading dialogLoading = getProgressDialogFromRequest(request);
        if (dialogLoading != null) {
            dialogLoading.setProgressMessage(text);
        }
    }

    private void processCancelListener(final Object object) {
        DialogCancelListener listener = cancelListenerMap.get(object);
        if (listener != null) {
            cancelListenerMap.remove(object);
            listener.onCancel();
        }
    }

    public void dismissProgressDialog(final Object object) {
        DialogLoading dialogLoading = getProgressDialogFromRequest(object);
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
