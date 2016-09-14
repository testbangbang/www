package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogLoading;

public abstract class BaseAction {

    private DialogLoading dialogLoading;

    public abstract void execute(final ReaderDataHolder readerDataHolder);

    public void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback){
    }

    public DialogLoading showLoadingDialog(final ReaderDataHolder holder, String title) {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(holder.getContext(),
                    title,
                    true, new DialogLoading.Callback() {
                @Override
                public void onCanceled() {
                }
            });
        }
        dialogLoading.show();
        return dialogLoading;
    }

    public DialogLoading showLoadingDialog(final ReaderDataHolder holder, int titleResId) {
        return showLoadingDialog(holder, holder.getContext().getString(titleResId));
    }

    public void hideLoadingDialog() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }
}
