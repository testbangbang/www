package com.onyx.kreader.ui.actions;

import android.app.Activity;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.request.ChangeLayoutRequest;
import com.onyx.kreader.reflow.ImageReflowSettings;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogReflowSettings;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class ImageReflowAction extends BaseAction {
    private DialogReflowSettings reflowSettingsDialog;

    public void execute(final ReaderDataHolder readerDataHolder) {
        showReflowSettingsDialog(readerDataHolder);
    }

    private void showReflowSettingsDialog(final ReaderDataHolder readerDataHolder) {
        Activity activity = (Activity) readerDataHolder.getContext();
        if (reflowSettingsDialog == null) {
            ImageReflowSettings settings = readerDataHolder.getReader().getImageReflowSettings();
            settings.dev_width = readerDataHolder.getDisplayWidth();
            settings.dev_height = readerDataHolder.getDisplayHeight();
            reflowSettingsDialog = new DialogReflowSettings(activity, settings, new DialogReflowSettings.ReflowCallback() {
                @Override
                public void onFinished(boolean confirm, ImageReflowSettings settings) {
                    if (confirm && settings != null) {
                        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.IMAGE_REFLOW_PAGE, new NavigationArgs());
                        readerDataHolder.submitRenderRequest(request);
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
