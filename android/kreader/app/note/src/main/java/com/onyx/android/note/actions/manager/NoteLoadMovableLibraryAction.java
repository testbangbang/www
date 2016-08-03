package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteLoadMovableLibraryRequest;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 17:44.
 */

public class NoteLoadMovableLibraryAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {
    static final String TAG = NoteLoadMovableLibraryAction.class.getSimpleName();
    private NoteLoadMovableLibraryRequest loadMovableLibraryRequest;

    public NoteLoadMovableLibraryAction(String currentLibID, List<String> excludeIDList) {
        this.currentLibID = currentLibID;
        this.excludeIDList = excludeIDList;
    }

    private List<String> excludeIDList;
    private String currentLibID;

    //TODO:if our behavior is same as mx,this should be default call back,other wise,just use the method with custom callback.
    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.showMovableFolderDialog(loadMovableLibraryRequest.getNoteList());
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        loadMovableLibraryRequest = new NoteLoadMovableLibraryRequest(currentLibID, excludeIDList);
        activity.submitRequest(loadMovableLibraryRequest, callback);
    }
}
