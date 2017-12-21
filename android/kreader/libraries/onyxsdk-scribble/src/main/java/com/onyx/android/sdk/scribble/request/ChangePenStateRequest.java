package com.onyx.android.sdk.scribble.request;

import com.onyx.android.sdk.scribble.NoteViewHelper;

/**
 * Created by lxm on 2017/12/20.
 */

public class ChangePenStateRequest extends BaseNoteRequest {

    private volatile boolean resume;
    private volatile boolean render;

    public ChangePenStateRequest(boolean resume, boolean render) {
        this.resume = resume;
        this.render = render;
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        setRender(render);
        setResumeInputProcessor(resume);
    }
}
