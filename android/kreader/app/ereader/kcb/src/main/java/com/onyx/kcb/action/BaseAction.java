package com.onyx.kcb.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.dialog.DialogLoading;
import com.onyx.kcb.holder.BaseDataHolder;

/**
 * Created by suicheng on 2017/4/14.
 */

public abstract class BaseAction<T extends BaseDataHolder> {
    private DialogLoading dialogLoading;

    public abstract void execute(T dataHolder, RxCallback baseCallback);

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
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }
}
