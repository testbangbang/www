package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.navigation.PagePrevRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoPrevPageAction extends BaseNoteAction {

    private boolean resume;

    public GotoPrevPageAction(boolean resume) {
        this.resume = resume;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final PagePrevRequest prevRequest = new PagePrevRequest(resume);
        noteViewHelper.submit(getAppContext(), prevRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(prevRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
