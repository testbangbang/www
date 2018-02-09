package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class ClearPageRequest extends ReaderBaseNoteRequest {

    private volatile List<PageInfo> pageInfo = new ArrayList<>();
    private boolean resumeRawInput = true;

    public ClearPageRequest(final List<PageInfo> p) {
        pageInfo.addAll(p);
        setVisiblePages(pageInfo);
    }

    public ClearPageRequest(final List<PageInfo> p, boolean resumeRawInput) {
        pageInfo.addAll(p);
        setVisiblePages(pageInfo);
        this.resumeRawInput = resumeRawInput;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        for (PageInfo p : pageInfo) {
            noteManager.getNoteDocument().clearPage(getContext(), p.getRange(),
                    p.getSubPage());
        }
        noteManager.setRenderBitmapDirty(true);
        renderVisiblePages(noteManager);
        getNoteDataInfo().setContentRendered(true);
        if (resumeRawInput) {
            setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
        }
    }
}
