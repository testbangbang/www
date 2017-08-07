package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeRemoveByGroupIdRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by ming on 12/16/16.
 */
public class RemoveByGroupIdAction extends BaseNoteAction {
    private String groupId;
    private boolean resume;

    public RemoveByGroupIdAction(String groupId, final boolean resume) {
        this.groupId = groupId;
        this.resume = resume;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        if (StringUtils.isNullOrEmpty(groupId)) {
            return;
        }
        ShapeRemoveByGroupIdRequest changeRequest = new ShapeRemoveByGroupIdRequest(groupId, resume);
        noteManager.submitRequest(changeRequest, callback);
    }
}
