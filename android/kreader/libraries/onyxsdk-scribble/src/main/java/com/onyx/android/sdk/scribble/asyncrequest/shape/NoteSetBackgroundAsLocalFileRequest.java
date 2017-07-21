package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by solskjaer49 on 16/7/5 14:50.
 */

public class NoteSetBackgroundAsLocalFileRequest extends AsyncBaseNoteRequest {
    private String localFilePath;

    public NoteSetBackgroundAsLocalFileRequest(String filePath , boolean resume) {
        localFilePath = filePath;
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.setBackground(NoteBackgroundType.FILE);
        parent.setBackgroundFilePath(localFilePath);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
