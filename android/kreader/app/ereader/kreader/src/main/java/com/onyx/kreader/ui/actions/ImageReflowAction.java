package com.onyx.kreader.ui.actions;

import android.content.DialogInterface;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.request.ChangeLayoutRequest;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogReflowSettings;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class ImageReflowAction extends BaseAction {
    private DialogReflowSettings reflowSettingsDialog;

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        showReflowSettingsDialog(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
    }

    private void showReflowSettingsDialog(final ReaderDataHolder readerDataHolder) {
        if (reflowSettingsDialog == null) {
            ImageReflowSettings settings = readerDataHolder.getReader().getImageReflowSettings();
            settings.dev_width = readerDataHolder.getDisplayWidth();
            settings.dev_height = readerDataHolder.getDisplayHeight();
            reflowSettingsDialog = new DialogReflowSettings(readerDataHolder, settings, new DialogReflowSettings.ReflowCallback() {
                @Override
                public void onFinished(boolean confirm, ImageReflowSettings settings) {
                    if (confirm && settings != null) {
                        readerDataHolder.getReader().getImageReflowManager().notifySettingsUpdated();
                        showLoadingDialog(readerDataHolder,R.string.reflowing);
                        hideReflowSettingsDialog(readerDataHolder);
                        BaseReaderRequest request = new ChangeLayoutRequest(PageConstants.IMAGE_REFLOW_PAGE, new NavigationArgs());
                        readerDataHolder.submitRenderRequest(request, new BaseCallback() {
                            @Override
                            public void done(BaseRequest request, Throwable e) {
                                hideLoadingDialog();
                            }
                        });
                    }
                }
            });
            readerDataHolder.trackDialog(reflowSettingsDialog);
            reflowSettingsDialog.show();
        }
    }

    private void hideReflowSettingsDialog(final ReaderDataHolder readerDataHolder) {
        if (reflowSettingsDialog != null) {
            reflowSettingsDialog.dismiss();
            reflowSettingsDialog = null;
        }
    }
}
