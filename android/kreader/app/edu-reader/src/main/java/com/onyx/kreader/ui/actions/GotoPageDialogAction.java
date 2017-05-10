package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogSetValue;

/**
 * Created by Joy on 2016/5/21.
 */
public class GotoPageDialogAction extends BaseAction {
    private DialogSetValue gotoPageDialog = null;

    @Override
    public void execute(ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        showGotoDialog(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
    }

    private DialogSetValue showGotoDialog(final ReaderDataHolder readerDataHolder) {
        if (gotoPageDialog == null) {
            DialogSetValue.DialogCallback callback = new DialogSetValue.DialogCallback() {
                @Override
                public void valueChange(int newValue) {
                    final GotoPageAction action = new GotoPageAction(newValue - 1);
                    action.execute(readerDataHolder);
                }

                @Override
                public void done(boolean isValueChange, int oldValue, int newValue) {
                    if (!isValueChange) {
                        final GotoPageAction action = new GotoPageAction(oldValue - 1);
                        action.execute(readerDataHolder);
                    }
                    dismissGotoDialog();
                }
            };
            gotoPageDialog = new DialogSetValue(readerDataHolder.getContext(), readerDataHolder.getCurrentPage() + 1, 1,
                    readerDataHolder.getPageCount(), true, true,
                    readerDataHolder.getContext().getString(R.string.go_to_page),
                    readerDataHolder.getContext().getString(R.string.go_to_page), callback);
        }
        gotoPageDialog.show();
        return gotoPageDialog;
    }

    private void dismissGotoDialog() {
        if (gotoPageDialog != null) {
            gotoPageDialog.dismiss();
            gotoPageDialog = null;
        }
    }

}
