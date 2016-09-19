package com.onyx.kreader.ui.actions;

import com.onyx.kreader.R;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.EmboldenGlyphRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogSetValue;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class EmboldenAction extends BaseAction {
    private DialogSetValue emboldenDialog;

    public void execute(final ReaderDataHolder readerDataHolder) {
        showEmboldenDialog(readerDataHolder);
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
                    hideEmboldenDialog();
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
        return emboldenDialog;
    }

    public DialogSetValue getEmboldenDialog() {
        return emboldenDialog;
    }

    private void hideEmboldenDialog() {
        if (emboldenDialog != null) {
            emboldenDialog.dismiss();
            emboldenDialog = null;
        }
    }
}
