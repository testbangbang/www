package com.onyx.android.sdk.scribble.asyncrequest;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.ShapeManagerOptions;

/**
 * Created by zhuzeng on 4/18/16.
 */
public class ConfigRequest extends AsyncBaseNoteRequest {

    private String documentMd5;
    private int initDisplayPage;

    public ConfigRequest(final String md5, int page, final ShapeManagerOptions option) {
        documentMd5 = md5;
        initDisplayPage = page;
    }

    public void execute(final NoteViewHelper parent) throws Exception {

    }

}
