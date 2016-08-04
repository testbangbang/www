package com.onyx.android.note.activity;

import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 16/8/1 17:59.
 */

public interface ScribbleInterface extends BaseInterface {
    void onRequestFinished(final BaseNoteRequest request, boolean updatePage);
}
