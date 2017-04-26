package com.onyx.kreader.action;

import android.content.Context;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.R;
import com.onyx.kreader.reader.data.ReaderDataHolder;

/**
 * Created by ming on 2017/4/25.
 */

public abstract class BaseAction {

    private MaterialDialog loadingDialog;

    public abstract void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback);

    public void showLoadingDialog(final ReaderDataHolder readerDataHolder, final int titleId, final int contentId) {
        if (loadingDialog == null) {
            loadingDialog = createProgressDialog(readerDataHolder.getContext(), titleId);
        }
        loadingDialog.show();
    }

    private MaterialDialog createProgressDialog(final Context context, final int titleId) {
        return new MaterialDialog.Builder(context)
                .title(titleId)
                .progress(true, 0)
                .build();
    }
}
