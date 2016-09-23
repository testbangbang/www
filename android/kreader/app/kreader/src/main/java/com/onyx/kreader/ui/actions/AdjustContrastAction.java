package com.onyx.kreader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.R;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.request.GammaCorrectionRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogSetValue;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class AdjustContrastAction extends BaseAction {

    private DialogSetValue contrastDialog;

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        showContrastDialog(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
    }

    public DialogSetValue showContrastDialog(final ReaderDataHolder readerDataHolder) {
        if (contrastDialog == null) {
            DialogSetValue.DialogCallback callback = new DialogSetValue.DialogCallback() {
                @Override
                public void valueChange(int newValue) {
                    final GammaCorrectionRequest request = new GammaCorrectionRequest(newValue);
                    request.setAbortPendingTasks(true);
                    readerDataHolder.submitRenderRequest(request);
                }

                @Override
                public void done(boolean isValueChange, int oldValue, int newValue) {
                    if (!isValueChange) {
                        GammaCorrectionRequest request = new GammaCorrectionRequest(oldValue);
                        readerDataHolder.submitRenderRequest(request);
                    }
                    hideContrastDialog(readerDataHolder);
                }
            };
            float current = readerDataHolder.getReader().getDocumentOptions().getGammaLevel();
            contrastDialog = new DialogSetValue(readerDataHolder.getContext(),
                    (int)current,
                    BaseOptions.minGammaLevel(), BaseOptions.maxGammaLevel(), true, true,
                    readerDataHolder.getContext().getString(R.string.dialog_reflow_settings_contrast),
                    readerDataHolder.getContext().getString(R.string.contrast_level), callback);

        }
        contrastDialog.show();
        readerDataHolder.addActiveDialog(contrastDialog);
        return contrastDialog;
    }

    private void hideContrastDialog(final ReaderDataHolder readerDataHolder) {
        if (contrastDialog != null) {
            contrastDialog.dismiss();
            readerDataHolder.removeActiveDialog(contrastDialog);
            contrastDialog = null;
        }
    }
}
