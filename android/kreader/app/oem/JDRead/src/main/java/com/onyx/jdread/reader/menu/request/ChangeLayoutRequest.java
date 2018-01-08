package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeLayoutRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ChangeLayoutParameter parameter;
    private ImageReflowSettings settings;

    public ChangeLayoutRequest(ReaderDataHolder readerDataHolder, ChangeLayoutParameter parameter, ImageReflowSettings settings) {
        this.readerDataHolder = readerDataHolder;
        this.parameter = parameter;
        this.settings = settings;
    }

    @Override
    public ChangeLayoutRequest call() throws Exception {
        updateSetting(readerDataHolder);
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().setCurrentLayout(parameter.getLayout(), parameter.getNavigationArgs());
        readerDataHolder.getReader().getReaderViewHelper().updatePageView(readerDataHolder);
        return this;
    }

    private void updateSetting(ReaderDataHolder readerDataHolder) {
        if (settings != null) {
            settings.dev_width = readerDataHolder.getReaderViewHelper().getPageViewWidth();
            settings.dev_height = readerDataHolder.getReaderViewHelper().getPageViewHeight();
            settings.justification = 3;
            readerDataHolder.getReader().getReaderHelper().getImageReflowManager().updateSettings(settings);
            readerDataHolder.getReader().getReaderHelper().getImageReflowManager().notifySettingsUpdated();
        }
    }
}
