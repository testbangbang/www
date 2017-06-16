package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageGoToTargetIndexRequest;

/**
 * Created by zhuzeng on 7/1/16.
 */
public class GotoTargetPageAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private PageGoToTargetIndexRequest prevRequest;
    private int targetPageIndex;
    private boolean resume;

    public GotoTargetPageAction(int index,boolean r) {
        this.targetPageIndex = index;
        resume = r;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(prevRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        prevRequest = new PageGoToTargetIndexRequest(targetPageIndex, resume);
        activity.submitRequest(prevRequest, callback);
    }
}
