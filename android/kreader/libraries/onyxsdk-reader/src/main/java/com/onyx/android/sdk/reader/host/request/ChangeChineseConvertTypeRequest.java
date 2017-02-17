package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ChangeChineseConvertTypeRequest extends BaseReaderRequest {

    private ReaderChineseConvertType convertType;

    public ChangeChineseConvertTypeRequest(final ReaderChineseConvertType convertType) {
        this.convertType = convertType;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getDocumentOptions().setChineseConvertType(convertType);
        saveReaderOptions(reader);
        reader.getRenderer().setChineseConvertType(convertType);
        reader.getBitmapCache().clear();
        drawVisiblePages(reader);
    }
}
