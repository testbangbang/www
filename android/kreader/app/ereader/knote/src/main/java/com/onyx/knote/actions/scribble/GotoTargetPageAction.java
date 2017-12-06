package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageGoToTargetIndexRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

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
        goToTargetIndexRequest.setRender(true);
        noteManager.submitRequest(goToTargetIndexRequest, callback);
    }
}
