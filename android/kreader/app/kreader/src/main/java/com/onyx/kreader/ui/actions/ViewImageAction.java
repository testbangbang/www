package com.onyx.kreader.ui.actions;

import android.graphics.Bitmap;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogImageView;

/**
 * Created by joy on 2/13/17.
 */

public class ViewImageAction extends BaseAction {
    private Bitmap bitmap;

    public ViewImageAction(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        Debug.e(getClass(), "execute: " + bitmap);
        new DialogImageView(readerDataHolder.getContext(), bitmap).show();
    }
}
