package com.onyx.edu.reader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.dialog.DialogLoading;

public abstract class BaseAction {

    private DialogLoading dialogLoading;

    public abstract void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback);

    public DialogLoading showLoadingDialog(final ReaderDataHolder holder, String title, DialogLoading.Callback callback) {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(holder.getContext(),
                    title, true, callback).disableProgress();
        }
        dialogLoading.show();
        return dialogLoading;
    }

    public DialogLoading showLoadingDialog(final ReaderDataHolder holder, int titleResId, DialogLoading.Callback callback) {
        return showLoadingDialog(holder, holder.getContext().getString(titleResId), callback);
    }

    public DialogLoading showLoadingDialog(final ReaderDataHolder holder, int titleResId) {
        return showLoadingDialog(holder, titleResId, null);
    }

    public void hideLoadingDialog() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }
}
