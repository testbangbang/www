package com.onyx.android.note.actions.manager;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseManagerActivity;
import com.onyx.android.note.utils.Constant;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.scribble.request.note.NoteLibraryLoadRequest;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class LoadNoteListAction<T extends BaseManagerActivity> extends BaseNoteAction<T> {
    private volatile String parentLibraryId;
    private NoteModel parentNoteModel;
    private NoteLibraryLoadRequest loadRequest;
    private
    @SortBy.SortByDef
    int sortBy;
    private
    @AscDescOrder.AscDescOrderDef
    int ascOrder;

    public LoadNoteListAction(final String id) {
        this(id, SortBy.UPDATED_AT, AscDescOrder.DESC);
    }

    public LoadNoteListAction(final String id, @SortBy.SortByDef int sortBy, @AscDescOrder.AscDescOrderDef int ascOrder) {
        parentLibraryId = id;
        this.sortBy = sortBy;
        this.ascOrder = ascOrder;
    }


    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.updateCurLibName(parentNoteModel == null ? "" : parentNoteModel.getTitle());
                activity.updateCurLibPath(parentNoteModel == null ? "" : parentNoteModel.getExtraAttributes());
                activity.updateUIWithNewNoteList(loadRequest.getNoteList());
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        loadRequest = new NoteLibraryLoadRequest(parentLibraryId, Constant.PERTIME_THUMBNAIL_LOAD_LIMIT, sortBy, ascOrder);
        activity.submitRequest(loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                parentNoteModel = loadRequest.getNoteModel();
                invoke(callback, request, e);
            }
        });
    }
}
