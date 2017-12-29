package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.shop.event.HideAllDialogEvent;
import com.onyx.jdread.shop.event.LoadingDialogEvent;

/**
 * Created by jackdeng on 2017/12/26.
 */

public abstract class BaseAction<T extends PersonalDataBundle> {

    public abstract void execute(T dataBundle, RxCallback rxCallback);

    public void showLoadingDialog(T dataBundle, int messageResId) {
        dataBundle.getEventBus().post(new LoadingDialogEvent(messageResId));
    }

    public void hideLoadingDialog(T dataBundle) {
        dataBundle.getEventBus().post(new HideAllDialogEvent());
    }
}