package com.onyx.jdread.reader.dialog;

import android.app.Dialog;

/**
 * Created by huxiaomao on 2018/1/22.
 */

public interface DialogSearchViewCallBack {
    Dialog getContent();
    void nextSearchResult();
    void preSearchResult();
    void searchBack();
    void searchData();
}