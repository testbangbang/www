package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;
import com.onyx.jdread.shop.model.ShopDataBundle;

public abstract class BaseAction<T extends ShopDataBundle> {

    public abstract void execute(T dataBundle, RxCallback rxCallback);

    public void showLoadingDialog(T dataBundle, int messageResId) {
        dataBundle.getEventBus().post(new LoadingDialogEvent(messageResId));
    }

    public void hideLoadingDialog(T dataBundle) {
        dataBundle.getEventBus().post(new HideAllDialogEvent());
    }
}