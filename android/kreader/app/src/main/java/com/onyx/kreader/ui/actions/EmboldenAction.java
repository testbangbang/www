package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.EmboldenGlyphRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogSetValue;
import com.onyx.kreader.R;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class EmboldenAction extends BaseAction {
    private DialogSetValue emboldenDialog;

    public void execute(final ReaderActivity readerActivity) {
        showEmboldenDialog(readerActivity);
    }

    public DialogSetValue showEmboldenDialog(final ReaderActivity readerActivity)  {
        if (emboldenDialog == null) {
            DialogSetValue.DialogCallback callback = new DialogSetValue.DialogCallback() {
                @Override
                public void valueChange(int newValue) {
                    EmboldenGlyphRequest request = new EmboldenGlyphRequest(newValue);
                    readerActivity.submitRenderRequest(request);
                }

                @Override
                public void done(boolean isValueChange, int oldValue, int newValue) {
                    if (!isValueChange) {
                        EmboldenGlyphRequest request = new EmboldenGlyphRequest(oldValue);
                        readerActivity.submitRenderRequest(request);
                    }
                    hideEmboldenDialog();
                }
            };
            int current = readerActivity.getReader().getBaseOptions().getEmboldenLevel();
            emboldenDialog = new DialogSetValue(readerActivity,
                    current,
                    BaseOptions.minEmboldenLevel(),
                    BaseOptions.maxEmboldenLevel(), true, true,
                    readerActivity.getString(R.string.embolden),
                    readerActivity.getString(R.string.embolden_level), callback);
        }
        emboldenDialog.show();
        return emboldenDialog;
    }

    private void hideEmboldenDialog() {
        if (emboldenDialog != null) {
            emboldenDialog.hide();
            emboldenDialog = null;
        }
    }
}
