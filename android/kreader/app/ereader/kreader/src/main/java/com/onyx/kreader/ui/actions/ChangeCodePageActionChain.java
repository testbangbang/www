package com.onyx.kreader.ui.actions;

import android.app.Activity;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;


/**
 * Created by zhuzeng on 9/22/16.
 */
public class ChangeCodePageActionChain extends BaseAction {
    private final Activity activity;
    private final int codePage;

    public ChangeCodePageActionChain(final Activity activity, final int codePage) {
        this.activity = activity;
        this.codePage = codePage;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final ActionChain actionChain = new ActionChain();
        actionChain.addAction(new ChangeCodePageAction(codePage));
        actionChain.addAction(new ReopenDocumentActionChain(activity));
        actionChain.execute(readerDataHolder, callback);
    }


}
