package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.request.navigation.PageGoToTargetIndexRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoTargetPageAction extends BaseNoteAction {
    private int targetPageIndex;
    private boolean resume;

    public GotoTargetPageAction(int index, boolean r) {
        this.targetPageIndex = index;
        resume = r;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        PageGoToTargetIndexRequest goToTargetIndexRequest = new PageGoToTargetIndexRequest(targetPageIndex, resume);
        goToTargetIndexRequest.setDrawToView(true);
        noteManager.submitRequest(goToTargetIndexRequest, callback);
    }
}
