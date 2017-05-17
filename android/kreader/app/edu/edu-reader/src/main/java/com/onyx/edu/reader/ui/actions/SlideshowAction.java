package com.onyx.edu.reader.ui.actions;

import android.widget.RelativeLayout;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.dialog.DialogSlideshowSettings;
import com.onyx.edu.reader.ui.handler.BaseHandler;
import com.onyx.edu.reader.ui.handler.HandlerManager;
import com.onyx.edu.reader.ui.handler.SlideshowHandler;

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
                        BaseHandler.HandlerInitialState state = SlideshowHandler.createInitialState(parent, maxPageCount, interval);
                        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.SLIDESHOW_PROVIDER, state);
                    }
                });
        readerDataHolder.trackDialog(dlg);
        dlg.show();
    }


}
