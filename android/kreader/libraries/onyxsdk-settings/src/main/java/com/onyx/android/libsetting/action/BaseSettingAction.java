package com.onyx.android.libsetting.action;

import android.content.Context;

import com.onyx.android.libsetting.SettingManager;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by solskjaer49 on 2017/2/11 19:02.
 */

public abstract class BaseSettingAction {
    public abstract void execute(final Context context,final SettingManager manager, final BaseCallback callback);
}
