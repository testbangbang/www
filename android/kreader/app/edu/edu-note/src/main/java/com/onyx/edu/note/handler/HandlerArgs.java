package com.onyx.edu.note.handler;

import android.net.Uri;

/**
 * Created by solskjaer49 on 2017/9/1 14:21.
 */

public class HandlerArgs {
    public Uri getEditPicUri() {
        return editPicUri;
    }

    public HandlerArgs setEditPicUri(Uri editPicUri) {
        this.editPicUri = editPicUri;
        return this;
    }

    private Uri editPicUri;
}
