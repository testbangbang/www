package com.onyx.jdread.reader.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.jdread.reader.utils.ReaderViewUtil;

/**
 * Created by hehai on 18-3-19.
 */

public class ReaderBaseDialog extends OnyxBaseDialog {

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
        ReaderViewUtil.clearFastModeByConfig();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ReaderViewUtil.applyFastModeByConfig();
    }
}
