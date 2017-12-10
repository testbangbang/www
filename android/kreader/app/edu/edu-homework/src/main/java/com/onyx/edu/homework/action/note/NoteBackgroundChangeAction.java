package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.shape.NoteBackgroundChangeRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteBackgroundChangeAction extends BaseNoteAction {

    private int backgroundType;
    private boolean resume;

    public NoteBackgroundChangeAction(int backgroundType ,boolean resume) {
        this.backgroundType = backgroundType;
        this.resume = resume;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        noteViewHelper.setBackground(backgroundType);
        final NoteBackgroundChangeRequest bgChangeRequest = new NoteBackgroundChangeRequest(backgroundType, resume);
        noteViewHelper.submit(getAppContext(), bgChangeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                noteViewHelper.post(RequestFinishedEvent.create(bgChangeRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
