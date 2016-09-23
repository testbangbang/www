package com.onyx.kreader.note.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.FlushShapeListRequest;
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/22/16.
 */
public class FlushNoteAction extends BaseAction {

    private List<PageInfo> visiblePages = new ArrayList<>();
    private boolean render;
    private boolean save;

    public FlushNoteAction(List<PageInfo> list, boolean r, boolean s) {
        render = r;
        save = s;
        if (list != null) {
            visiblePages.addAll(list);
        }
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        if (!readerDataHolder.isDocumentOpened()) {
            BaseCallback.invoke(callback, null, null);
            return;
        }

        final NoteManager noteManager = readerDataHolder.getNoteManager();
        final FlushShapeListRequest flushRequest = new FlushShapeListRequest(visiblePages, noteManager.detachShapeStash(), 0, render, save);
        noteManager.submit(readerDataHolder.getContext(), flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

}
