package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.FlushShapeListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.ShapeRenderFinishEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class FlushNoteAction extends BaseAction {

    private List<PageInfo> visiblePages = new ArrayList<>();
    private boolean render;
    private boolean save;
    private boolean showDialog;
    private boolean transfer;
    private boolean pauseNote = false;

    public FlushNoteAction(List<PageInfo> list, boolean renderShapes, boolean transferBitmap, boolean saveToDatabase, boolean show) {
        render = renderShapes;
        save = saveToDatabase;
        if (list != null) {
            visiblePages.addAll(list);
        }
        showDialog = show;
        transfer = transferBitmap;
    }

    public static FlushNoteAction pauseAfterFlush(List<PageInfo> list) {
        final FlushNoteAction flushNoteAction = new FlushNoteAction(list, true, true, false, false);
        flushNoteAction.pauseNote = true;
        return flushNoteAction;
    }

    public static FlushNoteAction resumeAfterFlush(List<PageInfo> list) {
        final FlushNoteAction flushNoteAction = new FlushNoteAction(list, true, true, false, false);
        return flushNoteAction;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final List<Shape> stash = noteManager.detachShapeStash();
        if (!readerDataHolder.isDocumentOpened() && !save && CollectionUtils.isNullOrEmpty(stash)) {
            BaseCallback.invoke(callback, null, null);
            return;
        }

        if (showDialog) {
            showLoadingDialog(readerDataHolder, R.string.saving);
        }

        final FlushShapeListRequest flushRequest = new FlushShapeListRequest(visiblePages, stash, 0, render, transfer, save);
        flushRequest.setPause(isPauseNote());
        final int id = readerDataHolder.getLastRequestSequence();
        noteManager.submitWithUniqueId(readerDataHolder.getContext(), id, flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (showDialog) {
                    hideLoadingDialog();
                }
                readerDataHolder.getEventBus().post(ShapeRenderFinishEvent.shapeReadyEventWithUniqueId(id));
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    public boolean isPauseNote() {
        return pauseNote;
    }

    public void setPauseNote(boolean pauseNote) {
        this.pauseNote = pauseNote;
    }
}
