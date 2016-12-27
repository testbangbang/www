package com.onyx.kreader.ui.actions;

import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogSlideshowSettings;
import com.onyx.kreader.ui.handler.HandlerManager;
import com.onyx.kreader.ui.handler.SlideshowHandler;

/**
 * Created by zhuzeng on 6/2/16.
 */
public class SlideshowAction extends BaseAction {

    private RelativeLayout parent;

    public SlideshowAction(RelativeLayout parent) {
        this.parent = parent;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        showSlideshowSettingsDialog(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
    }

    private void showSlideshowSettingsDialog(final ReaderDataHolder readerDataHolder) {
        DialogSlideshowSettings dlg = new DialogSlideshowSettings(readerDataHolder.getContext(),
                new DialogSlideshowSettings.Callback() {
                    @Override
                    public void done(int interval, int maxPageCount) {
                        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.SLIDESHOW_PROVIDER);
                        SlideshowHandler handler = (SlideshowHandler) readerDataHolder.getHandlerManager().getActiveProvider();
                        handler.setInterval(interval);
                        handler.start(parent, maxPageCount);
                    }
                });
        readerDataHolder.addActiveDialog(dlg);
        dlg.show();
    }


}
