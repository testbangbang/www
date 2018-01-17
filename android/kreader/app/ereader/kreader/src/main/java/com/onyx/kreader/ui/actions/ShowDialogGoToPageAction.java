package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.note.actions.ResumeDrawingAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogGotoPage;

/**
 * Created by joy on 7/15/16.
 */
public class ShowDialogGoToPageAction extends BaseAction {
    private static final String TAG = ShowDialogGoToPageAction.class.getSimpleName();

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        int posX = (readerDataHolder.getDisplayWidth() / 4);
        posX = readerDataHolder.getSideNoteArea() == ReaderDataHolder.SideNoteArea.LEFT
                ? posX : -posX;
        DialogGotoPage.show(readerDataHolder,
                true, null, posX, Integer.MIN_VALUE, new DialogGotoPage.OnCloseCallback() {
                    @Override
                    public void onClose() {
                        new ResumeDrawingAction(readerDataHolder.
                                getVisiblePages()).execute(readerDataHolder, null);
                    }
                });
        BaseCallback.invoke(callback, null, null);
    }
}
