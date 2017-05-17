package com.onyx.edu.reader.ui.dialog;

import android.content.Context;

import com.onyx.android.sdk.ui.dialog.OnyxBaseDialog;
import com.onyx.edu.reader.R;

public class DialogBase extends OnyxBaseDialog
{
    public DialogBase(Context context)
    {
        super(context, R.style.dialog_no_title);

        setCanceledOnTouchOutside(true);
    }

    public DialogBase(Context context, int style)
    {
        super(context, style);

        setCanceledOnTouchOutside(true);
    }
}