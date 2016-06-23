package com.onyx.android.sdk.scribble.request.navigation;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class PgaeGotoFirstRequest extends BaseNoteRequest {

    public void execute(final ShapeViewHelper helper) throws Exception {
        helper.getNoteDocument().gotoFirst();
        renderCurrentPage(helper);
    }

}
