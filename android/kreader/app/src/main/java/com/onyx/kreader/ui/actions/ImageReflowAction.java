package com.onyx.kreader.ui.actions;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.request.ChangeLayoutRequest;
import com.onyx.kreader.reflow.ImageReflowSettings;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogReflowSettings;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class ImageReflowAction extends BaseAction {
    private DialogReflowSettings reflowSettingsDialog;

    public void execute(final ReaderActivity readerActivity) {
        showReflowSettingsDialog(readerActivity);
    }

    private void showReflowSettingsDialog(final ReaderActivity readerActivity) {
        if (reflowSettingsDialog == null) {
            ImageReflowSettings settings = readerActivity.getReader().getImageReflowSettings();
            settings.dev_width = readerActivity.getDisplayWidth();
            settings.dev_height = readerActivity.getDisplayHeight();
            reflowSettingsDialog = new DialogReflowSettings(readerActivity, settings, new DialogReflowSettings.ReflowCallback() {
                @Override
                public void onFinished(boolean confirm, ImageReflowSettings settings) {
                    if (confirm && settings != null) {
                        BaseReaderRequest request = new ChangeLayoutRequest(ReaderConstants.IMAGE_REFLOW_PAGE, new NavigationArgs());
                        readerActivity.submitRenderRequest(request);
                    }
                    hideReflowSettingsDialog();
                }
            });
        }
        reflowSettingsDialog.show();
    }

    private void hideReflowSettingsDialog() {
        if (reflowSettingsDialog != null) {
            reflowSettingsDialog.hide();
            reflowSettingsDialog = null;
        }
    }
}
