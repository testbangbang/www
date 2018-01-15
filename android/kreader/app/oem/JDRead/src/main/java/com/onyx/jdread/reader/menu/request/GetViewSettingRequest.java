package com.onyx.jdread.reader.menu.request;

import android.content.Context;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class GetViewSettingRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderTextStyle style;
    private ImageReflowSettings settings;
    private ReaderViewInfo readerViewInfo;

    public GetViewSettingRequest(ReaderViewInfo readerViewInfo,ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public GetViewSettingRequest call() throws Exception {
        ReaderTextStyle srcStyle = readerDataHolder.getReader().getReaderHelper().getTextStyleManager().getStyle();
        style = ReaderTextStyle.copy(srcStyle);

        ImageReflowSettings srcSettings = readerDataHolder.getReader().getReaderHelper().getImageReflowManager().getSettings();
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
        getReaderUserDataInfo().setDocumentPath(readerDataHolder.getReader().getDocumentInfo().getBookPath());
        getReaderUserDataInfo().setDocumentCategory(readerDataHolder.getReader().getReaderHelper().getDocumentOptions().getDocumentCategory());
        getReaderUserDataInfo().setDocumentCodePage(readerDataHolder.getReader().getReaderHelper().getDocumentOptions().getCodePage());
        getReaderUserDataInfo().setChineseConvertType(readerDataHolder.getReader().getReaderHelper().getDocumentOptions().getChineseConvertType());
        getReaderUserDataInfo().setDocumentMetadata(readerDataHolder.getReader().getReaderHelper().getDocumentMetadata());


        boolean isSupportScale = readerDataHolder.getReader().getReaderHelper().getRendererFeatures().supportScale();
        String displayName = readerDataHolder.getReader().getReaderHelper().getPlugin().displayName();
        String md5 = readerDataHolder.getReader().getReaderHelper().getDocumentMd5();
        ReaderNavigator navigator = readerDataHolder.getReader().getReaderHelper().getNavigator();

        Context context = JDReadApplication.getInstance().getApplicationContext();
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
