package com.onyx.edu.homework.base;


import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.edu.homework.Global;

public abstract class BaseAction {

    private DialogLoading dialogLoading;

    public abstract void execute(final Context context, BaseCallback baseCallback);

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

    public CloudManager getCloudManager() {
        return Global.getInstance().getCloudManager();
    }

    public DataManager getDataManager() {
        return Global.getInstance().getDataManager();
    }
}
