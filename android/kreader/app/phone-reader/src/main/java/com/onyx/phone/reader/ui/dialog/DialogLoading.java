package com.onyx.phone.reader.ui.dialog;

/**
 * Created by suicheng on 2017/2/16.
 */
public interface DialogLoading {
    boolean isShowing();

    void dismiss();

    void show();

    void setProgress(int progress);
}
