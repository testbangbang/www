package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryRemoveRequest;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/27 19:35.
 */

public class NoteLibraryRemoveAction extends BaseNoteAction {
    List<String> targetRemoveLibraryList;

    public NoteLibraryRemoveAction(List<String> targetRemoveLibraryList) {
        this.targetRemoveLibraryList = targetRemoveLibraryList;
    }

    @Override
    public void execute(final ManageActivity activity) {
        for (String uniqueID : targetRemoveLibraryList) {
            final NoteLibraryRemoveRequest request = new NoteLibraryRemoveRequest(uniqueID);
            activity.getNoteViewHelper().submit(activity, request, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    //TODO:should use batch remove method
                    activity.loadNoteList();
                }
            });
        }
    }
}
