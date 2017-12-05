package com.onyx.android.plato.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;

/**
 * Created by zhuzeng on 6/26/16.
 */
public abstract class BaseNoteAction {
    public abstract void execute(final NoteManager noteManager, final BaseCallback callback);
}