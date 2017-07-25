package com.onyx.android.dr.reader.action;


import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.ui.dialog.DialogLoading;

public abstract class BaseAction {

    private DialogLoading dialogLoading;

    public abstract void execute(final ReaderPresenter readerPresenter, BaseCallback baseCallback);

    public DialogLoading showLoadingDialog(final ReaderPresenter presenter, String title) {
        if (dialogLoading == null) {
            dialogLoading = new DialogLoading(presenter.getReaderView().getViewContext(),
                    title, true);
        }
        dialogLoading.show();
        return dialogLoading;
    }

    public DialogLoading showLoadingDialog(final ReaderPresenter presenter, int titleResId) {
        return showLoadingDialog(presenter, presenter.getReaderView().getViewContext().getString(titleResId));
    }

    public void hideLoadingDialog() {
        if (dialogLoading != null) {
            dialogLoading.dismiss();
            dialogLoading = null;
        }
    }
}
