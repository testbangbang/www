package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.host.request.RenderRequest;
import com.onyx.edu.reader.device.ReaderDeviceManager;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/5/2.
 */

public class ToggleAnimationUpdateAction extends BaseAction {

    private boolean clear;

    public ToggleAnimationUpdateAction(boolean clear) {
        this.clear = clear;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, BaseCallback baseCallback) {
        ReaderDeviceManager.toggleAnimationUpdate(clear);
        RenderRequest renderRequest = new RenderRequest();
        readerDataHolder.submitRenderRequest(renderRequest);
    }
}
