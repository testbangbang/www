package com.onyx.jdread.reader.menu.request;

import android.content.Context;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class GetViewSettingRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderViewInfo readerViewInfo;

    public GetViewSettingRequest(ReaderViewInfo readerViewInfo,Reader reader) {
        this.reader = reader;
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public GetViewSettingRequest call() throws Exception {
        LayoutProviderUtils.updateReaderViewInfo(reader,getReaderViewInfo(),reader.getReaderHelper().getReaderLayoutManager());
        updateSetting(reader);
        return this;
    }
}
