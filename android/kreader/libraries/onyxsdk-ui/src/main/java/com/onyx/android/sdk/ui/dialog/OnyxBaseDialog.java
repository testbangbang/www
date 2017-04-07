package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import com.onyx.android.sdk.api.device.epd.EpdController;

/**
 * Created by wangxu on 17-3-16.
 */

public class OnyxBaseDialog extends Dialog {

    public OnyxBaseDialog(Context context) {
        super(context);
    }

    public OnyxBaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected OnyxBaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        EpdController.disableRegal();
        super.show();
    }

    @Override
    public void dismiss() {
        EpdController.enableRegal();
        super.dismiss();
    }

    @Override
    public void hide() {
        EpdController.enableRegal();
        super.hide();
    }
}
