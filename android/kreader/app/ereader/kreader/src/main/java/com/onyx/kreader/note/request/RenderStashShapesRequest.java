package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class RenderStashShapesRequest extends ReaderBaseNoteRequest {

    public RenderStashShapesRequest(final List<PageInfo> pages) {
        setPauseRawInputProcessor(false);
        setResumeRawInputProcessor(true);
        setRender(true);
        setVisiblePages(pages);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        super.execute(noteManager);
    }
}
