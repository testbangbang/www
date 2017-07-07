package com.onyx.android.dr.holder;

import android.content.Context;

/**
 * Created by suicheng on 2017/4/15.
 */

public class BaseDataHolder {

    private Context context;

    public BaseDataHolder(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
