package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class FlushAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile List<Shape> shapeListd;

    public FlushAction(final List<Shape> list) {
        shapeListd = list;
    }

    public void execute(final T activity) {
        final PageFlushRequest flushRequest = new PageFlushRequest(shapeListd);
        activity.getNoteViewHelper().submit(activity, flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(true);
            }
        });
    }
}
