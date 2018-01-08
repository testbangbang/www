package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class GetDocumentSettingRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderTextStyle style;
    private ImageReflowSettings settings;

    public GetDocumentSettingRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public GetDocumentSettingRequest call() throws Exception {
        ReaderTextStyle srcStyle = readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle();
        style = ReaderTextStyle.copy(srcStyle);

        ImageReflowSettings srcSettings = readerDataHolder.getReader().getReaderHelper().getImageReflowManager().getSettings();
        settings = ImageReflowSettings.copy(srcSettings);
        return this;
    }

    public ReaderTextStyle getStyle() {
        return style;
    }

    public ImageReflowSettings getSettings() {
        return settings;
    }
}
