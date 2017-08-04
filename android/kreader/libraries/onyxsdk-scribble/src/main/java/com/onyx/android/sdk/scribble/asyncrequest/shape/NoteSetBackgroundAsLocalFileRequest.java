package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;

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

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        parent.setBackground(NoteBackgroundType.FILE);
        parent.setBackgroundFilePath(localFilePath);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
