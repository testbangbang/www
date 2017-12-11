package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.shape.PenWidthChangeRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteStrokeWidthChangeAction extends BaseNoteAction {

    private float strokeWidth;

    public NoteStrokeWidthChangeAction(float sw) {
        this.strokeWidth = sw;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final PenWidthChangeRequest penWidthChangeRequest = new PenWidthChangeRequest(strokeWidth);
        noteViewHelper.submit(getAppContext(), penWidthChangeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(penWidthChangeRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
