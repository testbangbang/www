package com.onyx.jdread.reader.catalog.dialog;

import android.app.Dialog;

import com.onyx.jdread.reader.common.ReaderUserDataInfo;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public interface ReaderBookInfoViewBack {
    Dialog getContent();
    void updateView(ReaderUserDataInfo readerUserDataInfo);
}
