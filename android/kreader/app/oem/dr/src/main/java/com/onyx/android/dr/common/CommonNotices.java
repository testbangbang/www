package com.onyx.android.dr.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by hehai on 17-6-28.
 */

public class CommonNotices {
    public static void showMessage(final Context context, final String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
