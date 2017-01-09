package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByGroupIdRequest;
import com.onyx.android.sdk.scribble.request.shape.ShapeRemoveByPointListRequest;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by ming on 12/16/16.
 */
public class RemoveByGroupIdAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private ShapeRemoveByGroupIdRequest changeRequest;
    private String groupId;
    private boolean resume;

    public RemoveByGroupIdAction(String groupId, final boolean resume) {
        this.groupId = groupId;
        this.resume = resume;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(changeRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        if (StringUtils.isNullOrEmpty(groupId)) {
            return;
        }
        changeRequest = new ShapeRemoveByGroupIdRequest(groupId, resume);
        activity.submitRequest(changeRequest, callback);
    }

}
