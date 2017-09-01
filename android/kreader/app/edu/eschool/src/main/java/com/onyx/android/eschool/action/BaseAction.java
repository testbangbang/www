package com.onyx.android.eschool.action;

import com.onyx.android.eschool.holder.BaseDataHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.ui.dialog.DialogLoading;

/**
 * Created by suicheng on 2017/4/14.
 */

public abstract class BaseAction<T extends BaseDataHolder> {
    private DialogLoading dialogLoading;

    public abstract void execute(T dataHolder, BaseCallback baseCallback);

    public DialogLoading showLoadingDialog(T dataHolder, String title) {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(dataHolder.getContext(),
                    title, false);
        }
        dialogLoading.show();
        return dialogLoading;
    }

    public DialogLoading showLoadingDialog(T dataHolder, int titleResId) {
        return showLoadingDialog(dataHolder, dataHolder.getContext().getString(titleResId));
    }

    public void hideLoadingDialog() {
        if (dialogLoading != null && dialogLoading.isShowing()) {
            try {
                dialogLoading.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            dialogLoading = null;
        }
    }
}
