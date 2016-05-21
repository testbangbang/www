package com.onyx.kreader.ui.actions;

import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.GammaCorrectionRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogSetValue;
import com.onyx.kreader.R;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class AdjustContrastAction extends BaseAction {

    private DialogSetValue contrastDialog;

    public void execute(final ReaderActivity readerActivity) {
        showContrastDialog(readerActivity);
    }

    public DialogSetValue showContrastDialog(final ReaderActivity readerActivity) {
        if (contrastDialog == null) {
            DialogSetValue.DialogCallback callback = new DialogSetValue.DialogCallback() {
                @Override
                public void valueChange(int newValue) {
                    final GammaCorrectionRequest request = new GammaCorrectionRequest(newValue);
                    readerActivity.submitRenderRequest(request);
                }

                @Override
                public void done(boolean isValueChange, int oldValue, int newValue) {
                    if (!isValueChange) {
                        GammaCorrectionRequest request = new GammaCorrectionRequest(oldValue);
                        readerActivity.submitRenderRequest(request);
                    }
                    hideContrastDialog();
                }
            };
            float current = readerActivity.getReader().getDocumentOptions().getGammaLevel();
            contrastDialog = new DialogSetValue(readerActivity,
                    (int)current,
                    BaseOptions.minGammaLevel(), BaseOptions.maxGammaLevel(), true, true,
                    readerActivity.getString(R.string.dialog_reflow_settings_contrast),
                    readerActivity.getString(R.string.contrast_level), callback);

        }
        contrastDialog.show();
        return contrastDialog;
    }

    private void hideContrastDialog() {
        if (contrastDialog != null) {
            contrastDialog.hide();
            contrastDialog = null;
        }
    }
}
