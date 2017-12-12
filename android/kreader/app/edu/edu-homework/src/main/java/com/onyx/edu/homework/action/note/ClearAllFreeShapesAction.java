package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.navigation.ClearAllFreeShapesRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by zhuzeng on 8/7/16.
 */
public class ClearAllFreeShapesAction extends BaseNoteAction {

    private boolean resume;

    public ClearAllFreeShapesAction(boolean r) {
        resume = r;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final ClearAllFreeShapesRequest clearAllFreeShapesRequest = new ClearAllFreeShapesRequest(resume);
//        activity.setFullUpdate(true);
        noteViewHelper.submit(getAppContext(), clearAllFreeShapesRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(clearAllFreeShapesRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
