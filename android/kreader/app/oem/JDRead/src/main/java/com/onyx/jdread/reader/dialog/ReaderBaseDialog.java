package com.onyx.jdread.reader.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;

/**
 * Created by hehai on 18-3-19.
 */

public class ReaderBaseDialog extends Dialog {

    public ReaderBaseDialog(@NonNull Context context) {
        super(context);
    }

    public ReaderBaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ReaderBaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();
        if (JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key, false)) {
            EpdController.applyApplicationFastMode(getClass().getSimpleName(), false, false);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (JDPreferenceManager.getBooleanValue(R.string.speed_refresh_key, false)) {
            EpdController.applyApplicationFastMode(getClass().getSimpleName(), true, false);
        }
    }
}
