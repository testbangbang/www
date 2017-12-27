package com.onyx.jdread.reader.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.onyx.jdread.reader.ui.ReaderActivity;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class OpenBookHelper {
    private static final String TAG = OpenBookHelper.class.getSimpleName();

    public static void openBook(Context context, DocumentInfo documentInfo) {
        Intent intent = new Intent();
        documentInfo.documentInfoToIntent(intent);
        intent.setComponent(new ComponentName(context, ReaderActivity.class));
        context.startActivity(intent);
    }
}
