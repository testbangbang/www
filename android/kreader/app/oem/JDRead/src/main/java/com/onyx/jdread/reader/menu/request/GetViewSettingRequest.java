package com.onyx.jdread.reader.menu.request;

import android.content.Context;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class GetViewSettingRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderTextStyle style;
    private ImageReflowSettings settings;
    private ReaderViewInfo readerViewInfo;

    public GetViewSettingRequest(ReaderViewInfo readerViewInfo,Reader reader) {
        this.reader = reader;
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public GetViewSettingRequest call() throws Exception {
        ReaderTextStyle srcStyle = reader.getReaderHelper().getTextStyleManager().getStyle();
        style = ReaderTextStyle.copy(srcStyle);

        ImageReflowSettings srcSettings = reader.getReaderHelper().getImageReflowManager().getSettings();
        settings = ImageReflowSettings.copy(srcSettings);

        loadUserData();
        return this;
    }

    public ReaderTextStyle getStyle() {
        return style;
    }

    public ImageReflowSettings getSettings() {
        return settings;
    }

    private void loadUserData() {
        getReaderUserDataInfo().setDocumentPath(reader.getDocumentInfo().getBookPath());
        getReaderUserDataInfo().setDocumentCategory(reader.getReaderHelper().getDocumentOptions().getDocumentCategory());
        getReaderUserDataInfo().setDocumentCodePage(reader.getReaderHelper().getDocumentOptions().getCodePage());
        getReaderUserDataInfo().setChineseConvertType(reader.getReaderHelper().getDocumentOptions().getChineseConvertType());
        getReaderUserDataInfo().setDocumentMetadata(reader.getReaderHelper().getDocumentMetadata());


        boolean isSupportScale = reader.getReaderHelper().getRendererFeatures().supportScale();
        String displayName = reader.getReaderHelper().getPlugin().displayName();
        String md5 = reader.getReaderHelper().getDocumentMd5();
        ReaderNavigator navigator = reader.getReaderHelper().getNavigator();

        Context context = reader.getReaderHelper().getContext();
        if (readerViewInfo != null) {
            getReaderUserDataInfo().loadPageBookmarks(context, isSupportScale, displayName, md5, navigator, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null) {
            getReaderUserDataInfo().loadPageBookmarks(context, isSupportScale, displayName, md5, navigator, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null) {
            getReaderUserDataInfo().loadPageLinks(context, navigator, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null) {
            getReaderUserDataInfo().loadPageImages(context, navigator, readerViewInfo.getVisiblePages());
        }
    }
}
