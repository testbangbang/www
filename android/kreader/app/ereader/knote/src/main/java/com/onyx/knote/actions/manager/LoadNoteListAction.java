package com.onyx.knote.actions.manager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.data.AscDescOrder;
import com.onyx.android.sdk.scribble.data.SortBy;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteLibraryLoadRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;
import com.onyx.knote.util.Constant;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class LoadNoteListAction extends BaseNoteAction {
    private volatile String parentLibraryId;
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

    @Override
    public void execute(final NoteManager manager, BaseCallback callback) {
        NoteLibraryLoadRequest loadRequest = new NoteLibraryLoadRequest(parentLibraryId,
                Constant.PER_PAGE_THUMBNAIL_LOAD_LIMIT, sortBy, ascOrder);
        loadRequest.setRender(false);
        manager.submitRequest(loadRequest, callback);
    }
}
