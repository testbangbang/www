package com.onyx.edu.student.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.edu.student.holder.BaseDataHolder;

/**
 * Created by suicheng on 2017/10/28.
 */

public abstract class BaseAction<T extends BaseDataHolder> {

    public abstract void execute(Context context, T dataHolder, BaseCallback baseCallback);
}
