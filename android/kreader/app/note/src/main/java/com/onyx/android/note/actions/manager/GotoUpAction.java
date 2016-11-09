package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryLoadRequest;

import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class GotoUpAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {

    private volatile String uniqueId;
    private NoteModel noteModel, parentModel;
    private NoteLibraryLoadRequest loadRequest;

    public GotoUpAction(final String id) {
        uniqueId = id;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.updateCurLibID(noteModel.getParentUniqueId());
                activity.updateCurLibName(parentModel == null ? "" : parentModel.getTitle());
                activity.updateCurLibPath(parentModel == null ? "" : parentModel.getExtraAttributes());
                activity.updateUIWithNewNoteList(loadRequest.getNoteList());
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        noteModel = NoteDataProvider.load(activity, uniqueId);
        parentModel = NoteDataProvider.load(activity, noteModel.getParentUniqueId());
        if (parentModel != null) {
            parentModel.setExtraAttributes(
                    NoteDataProvider.getNoteAbsolutePath(getContext(), parentModel.getUniqueId(), "/"));
        }
        loadRequest = new NoteLibraryLoadRequest(noteModel.getParentUniqueId());
        loadRequest.thumbnailLimit = Constant.PERTIME_THUMBNAIL_LOAD_LIMIT;
        activity.submitRequest(loadRequest, callback);
    }
}
