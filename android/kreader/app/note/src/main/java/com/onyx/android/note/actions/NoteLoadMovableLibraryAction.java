package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ManageActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLoadMovableLibraryRequest;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 17:44.
 */

public class NoteLoadMovableLibraryAction<T extends ManageActivity> extends BaseNoteAction<T> {
    static final String TAG = NoteLoadMovableLibraryAction.class.getSimpleName();

    public NoteLoadMovableLibraryAction(String currentLibID, List<String> excludeIDList) {
        this.currentLibID = currentLibID;
        this.excludeIDList = excludeIDList;
    }

    private List<String> excludeIDList;
    private String currentLibID;

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteLoadMovableLibraryRequest loadMovableLibraryRequest = new NoteLoadMovableLibraryRequest(currentLibID, excludeIDList);
        activity.getNoteViewHelper().submit(activity, loadMovableLibraryRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.showMoveFolderDialog(loadMovableLibraryRequest.getNoteList());
            }
        });
    }
}
