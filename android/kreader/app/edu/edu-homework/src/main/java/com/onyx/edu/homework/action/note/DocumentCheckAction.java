package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.request.DocumentCheckRequest;

/**
 * Created by lxm on 2017/12/6.
 */

public class DocumentCheckAction extends BaseNoteAction {

    private String uniqueId;
    private String parentUniqueId;

    public DocumentCheckAction(String uniqueId, String parentUniqueId) {
        this.uniqueId = uniqueId;
        this.parentUniqueId = parentUniqueId;
    }

    @Override
    public void execute(NoteViewHelper noteViewHelper, BaseCallback baseCallback) {
        DocumentCheckRequest checkRequest = new DocumentCheckRequest(uniqueId, parentUniqueId);
        noteViewHelper.submit(getAppContext(), checkRequest, baseCallback);
    }
}
