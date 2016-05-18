package com.onyx.android.sdk.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import com.onyx.android.sdk.ui.R;

public class DialogBaseOnyx extends Dialog
{
    public DialogBaseOnyx(Context context)
    {
        super(context, R.style.dialog_no_title);

        setCanceledOnTouchOutside(true);
    }

    public DialogBaseOnyx(Context context, int style)
    {
        super(context, style);

        setCanceledOnTouchOutside(true);
    }
}