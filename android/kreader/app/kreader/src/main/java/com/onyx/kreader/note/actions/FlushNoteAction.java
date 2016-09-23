package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.R;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.FlushShapeListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.events.RequestFinishEvent;

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

    public FlushNoteAction(List<PageInfo> list, boolean r, boolean s, boolean show) {
        render = r;
        save = s;
        if (list != null) {
            visiblePages.addAll(list);
        }
        showDialog = show;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final List<Shape> stash = noteManager.detachShapeStash();
        if (!readerDataHolder.isDocumentOpened() || stash.isEmpty()) {
            BaseCallback.invoke(callback, null, null);
            return;
        }
        if (showDialog) {
            showLoadingDialog(readerDataHolder, R.string.saving);
        }

        final FlushShapeListRequest flushRequest = new FlushShapeListRequest(visiblePages, stash, 0, render, save);
        noteManager.submit(readerDataHolder.getContext(), flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (showDialog) {
                    hideLoadingDialog();
                }
                readerDataHolder.getEventBus().post(RequestFinishEvent.requestWithoutGCIntervalApply());
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

}
