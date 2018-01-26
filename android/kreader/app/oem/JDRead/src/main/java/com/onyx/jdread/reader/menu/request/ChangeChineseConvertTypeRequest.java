package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeChineseConvertTypeRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderChineseConvertType convertType;

    public ChangeChineseConvertTypeRequest(Reader reader, ReaderChineseConvertType convertType) {
        this.reader = reader;
        this.convertType = convertType;
    }

    @Override
    public ChangeChineseConvertTypeRequest call() throws Exception {
        reader.getReaderHelper().getRenderer().setChineseConvertType(convertType);
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        return this;
    }
}
