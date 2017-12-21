package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.library.event.HideAllDialogEvent;
import com.onyx.jdread.library.event.LoadingDialogEvent;
import com.onyx.jdread.setting.model.SettingBundle;

/**
 * Created by li on 2017/12/20.
 */

public abstract class BaseAction<T extends SettingBundle> {
    public abstract void excute(T bundle, RxCallback callback);

    public void showLoadingDialog(T bundle, int messageId) {
        bundle.getEventBus().post(new LoadingDialogEvent(messageId));
    }

    public void hideLoadingDialog(T bundle) {
        bundle.getEventBus().post(new HideAllDialogEvent());
    }
}
