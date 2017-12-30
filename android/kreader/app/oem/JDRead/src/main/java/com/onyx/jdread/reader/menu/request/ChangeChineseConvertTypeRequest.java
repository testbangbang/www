package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeChineseConvertTypeRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderChineseConvertType convertType;

    public ChangeChineseConvertTypeRequest(ReaderDataHolder readerDataHolder, ReaderChineseConvertType convertType) {
        this.readerDataHolder = readerDataHolder;
        this.convertType = convertType;
    }

    @Override
    public ChangeChineseConvertTypeRequest call() throws Exception {
        readerDataHolder.getReader().getReaderHelper().getRenderer().setChineseConvertType(convertType);
        readerDataHolder.getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }
}
