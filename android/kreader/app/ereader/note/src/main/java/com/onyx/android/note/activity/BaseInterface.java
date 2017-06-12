package com.onyx.android.note.activity;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by solskjaer49 on 16/8/3 11:46.
 */

public interface BaseInterface {
    void submitRequest(BaseNoteRequest request, BaseCallback callback);

    void submitRequestWithIdentifier(String identifier, BaseNoteRequest request, BaseCallback callback);
}
