package com.onyx.kreader.ui.actions;

import android.content.DialogInterface;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.request.EmboldenGlyphRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogSetValue;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class EmboldenAction extends BaseAction {
    private DialogSetValue emboldenDialog;

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        showEmboldenDialog(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
    }

    public DialogSetValue showEmboldenDialog(final ReaderDataHolder readerDataHolder)  {
        if (emboldenDialog == null) {
            DialogSetValue.DialogCallback callback = new DialogSetValue.DialogCallback() {
                @Override
                public void valueChange(int newValue) {
                    EmboldenGlyphRequest request = new EmboldenGlyphRequest(newValue);
                    readerDataHolder.submitRenderRequest(request);
                }

                @Override
                public void done(boolean isValueChange, int oldValue, int newValue) {
                    if (!isValueChange) {
                        EmboldenGlyphRequest request = new EmboldenGlyphRequest(oldValue);
                        readerDataHolder.submitRenderRequest(request);
                    }
                    hideEmboldenDialog(readerDataHolder);
                }
            };
            int current = readerDataHolder.getReader().getDocumentOptions().getEmboldenLevel();
            emboldenDialog = new DialogSetValue(readerDataHolder.getContext(),
                    current,
                    BaseOptions.minEmboldenLevel(),
                    BaseOptions.maxEmboldenLevel(), true, true,
                    readerDataHolder.getContext().getString(R.string.embolden),
                    readerDataHolder.getContext().getString(R.string.embolden_level), callback);
        }
        emboldenDialog.show();
        readerDataHolder.trackDialog(emboldenDialog);
        return emboldenDialog;
    }

    private void hideEmboldenDialog(final ReaderDataHolder readerDataHolder) {
        if (emboldenDialog != null) {
            emboldenDialog.dismiss();
            emboldenDialog = null;
        }
    }
}
