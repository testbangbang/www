package com.onyx.android.eschool.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.data.utils.MetadataUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;

/**
 * Created by suicheng on 2018/1/6.
 */
public class IntentUtils {

    public static boolean startToHomework(Context context, Object extraData) {
        Intent intent = new Intent();
        intent.setComponent(ViewDocumentUtils.getHomeworkAppComponent());
        MetadataUtils.putIntentExtraData(intent, extraData);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return ActivityUtil.startActivitySafely(context, intent);
    }
}
