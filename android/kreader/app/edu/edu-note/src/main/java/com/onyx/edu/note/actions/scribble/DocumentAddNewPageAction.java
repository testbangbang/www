package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageAddRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 6/30/16.
 * @param mAddPosition page will add to target position,
 *                     if this value <0,will add page to the end of current page.
 */
public class DocumentAddNewPageAction extends BaseNoteAction {
    private int mAddPosition;

    public DocumentAddNewPageAction() {
        this(-1);
    }

    public DocumentAddNewPageAction(int addPosition) {
        mAddPosition = addPosition;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        PageAddRequest pageAddRequest = new PageAddRequest(mAddPosition);
        pageAddRequest.setRender(true);
        noteManager.submitRequest(pageAddRequest, callback);
    }
}
