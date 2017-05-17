package com.onyx.edu.reader.ui.actions;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.host.request.ChangeChineseConvertTypeRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;


/**
 * Created by zhuzeng on 9/22/16.
 */
public class ChangeChineseConvertTypeAction extends BaseAction {
    private ReaderChineseConvertType convertType;

    public ChangeChineseConvertTypeAction(final ReaderChineseConvertType convertType) {
        this.convertType = convertType;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        readerDataHolder.submitRenderRequest(new ChangeChineseConvertTypeRequest(convertType), callback);
    }


}
