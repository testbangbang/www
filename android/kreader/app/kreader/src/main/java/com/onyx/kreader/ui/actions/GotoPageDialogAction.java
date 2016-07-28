package com.onyx.kreader.ui.actions;

import com.onyx.kreader.R;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogSetValue;

/**
 * Created by Joy on 2016/5/21.
 */
public class GotoPageDialogAction extends BaseAction {
    private DialogSetValue gotoPageDialog = null;

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        showGotoDialog(readerDataHolder);
    }

    private DialogSetValue showGotoDialog(final ReaderDataHolder readerDataHolder) {
        if (gotoPageDialog == null) {
            DialogSetValue.DialogCallback callback = new DialogSetValue.DialogCallback() {
                @Override
                public void valueChange(int newValue) {
                    final GotoPageAction action = new GotoPageAction(String.valueOf(newValue - 1));
                    action.execute(readerDataHolder);
                }

                @Override
                public void done(boolean isValueChange, int oldValue, int newValue) {
                    if (!isValueChange) {
                        final GotoPageAction action = new GotoPageAction(String.valueOf(oldValue - 1));
                        action.execute(readerDataHolder);
                    }
                    hideGotoDialog();
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

    private void hideGotoDialog() {
        if (gotoPageDialog != null) {
            gotoPageDialog.hide();
            gotoPageDialog = null;
        }
    }

}
