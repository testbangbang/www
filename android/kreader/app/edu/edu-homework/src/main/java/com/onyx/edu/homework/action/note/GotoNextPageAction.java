package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.navigation.PageNextRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoNextPageAction extends BaseNoteAction {

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final PageNextRequest nextRequest = new PageNextRequest();
        noteViewHelper.submit(getAppContext(), nextRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(nextRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
