package com.onyx.jdread.main.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.library.event.HideAllDialogEvent;
import com.onyx.jdread.library.event.LoadingDialogEvent;
import com.onyx.jdread.main.model.MainBundle;


/**
 * Created by suicheng on 2017/4/14.
 */

public abstract class BaseAction<T extends MainBundle> {

    public abstract void execute(T dataBundle, RxCallback baseCallback);

    public void showLoadingDialog(T dataBundle, int messageResId) {
        dataBundle.getEventBus().post(new LoadingDialogEvent(messageResId));
    }

    public void showLoadingDialog(T dataBundle, String message) {
        dataBundle.getEventBus().post(new LoadingDialogEvent(message));
    }

    public void hideLoadingDialog(T dataBundle) {
        dataBundle.getEventBus().post(new HideAllDialogEvent());
    }
}
