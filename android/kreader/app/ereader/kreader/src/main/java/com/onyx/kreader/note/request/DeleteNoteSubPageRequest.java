package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class DeleteNoteSubPageRequest extends ReaderBaseNoteRequest {

    private PageInfo pageInfo;
    private int subPageIndex;

    public DeleteNoteSubPageRequest(final PageInfo pageInfo, final int subPageIndex) {
        this.pageInfo = pageInfo;
        this.subPageIndex = subPageIndex;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        setResumeRawInputProcessor(true);
        noteManager.getNoteDocument().removePage(getContext(), pageInfo.getRange(),
                subPageIndex);
    }
}
