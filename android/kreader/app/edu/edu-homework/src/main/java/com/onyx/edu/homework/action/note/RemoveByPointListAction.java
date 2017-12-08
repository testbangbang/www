package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByPointListRequest;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class RemoveByPointListAction extends BaseNoteAction {
    private ShapeRemoveByPointListRequest changeRequest;
    private volatile TouchPointList touchPointList;

    public RemoveByPointListAction(final TouchPointList list) {
        touchPointList = list;
    }


    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        if (touchPointList == null) {
            return;
        }
        changeRequest = new ShapeRemoveByPointListRequest(touchPointList);
        noteViewHelper.submit(getAppContext(), changeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                noteViewHelper.post(RequestFinishedEvent.create(changeRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

}
