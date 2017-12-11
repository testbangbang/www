package com.onyx.edu.homework.base;


import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.edu.homework.Global;

public abstract class BaseNoteAction {

    private DialogLoading dialogLoading;
    private static Context context;

    public abstract void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback);

    public DialogLoading showLoadingDialog(final Context context, String title) {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(context,
                    title, true);
        }
        dialogLoading.show();
        return dialogLoading;
    }

    public DialogLoading showLoadingDialog(final Context context, int titleResId) {
        return showLoadingDialog(context, context.getString(titleResId));
    }
    public void hideLoadingDialog() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }

    public static void setAppContext(Context context) {
        BaseNoteAction.context = context.getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }

    public static void post(Object event) {
        Global.getInstance().post(event);
    }
}
