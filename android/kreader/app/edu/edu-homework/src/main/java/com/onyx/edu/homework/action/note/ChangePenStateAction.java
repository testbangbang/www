package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.ChangePenStateRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by lxm on 2017/12/20.
 */

public class ChangePenStateAction extends BaseNoteAction {

    private boolean resume;
    private boolean render;

    public ChangePenStateAction(boolean resume, boolean render) {
        this.resume = resume;
        this.render = render;
    }

    @Override
    public void execute(NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final ChangePenStateRequest penStateRequest = new ChangePenStateRequest(resume, render);
        noteViewHelper.submit(getAppContext(), penStateRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(penStateRequest, e, render));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
