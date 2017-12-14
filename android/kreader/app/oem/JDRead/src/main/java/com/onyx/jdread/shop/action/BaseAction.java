package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.model.StoreDataBundle;

public abstract class BaseAction<T extends StoreDataBundle> {

    public abstract void execute(T dataBundle, RxCallback baseCallback);

    public void showLoadingDialog(T dataBundle, int messageResId) {
        dataBundle.getEventBus().post(new LoadingDialogEvent(messageResId));
    }

    public void hideLoadingDialog(T dataBundle) {
        dataBundle.getEventBus().post(new HideAllDialogEvent());
    }
}