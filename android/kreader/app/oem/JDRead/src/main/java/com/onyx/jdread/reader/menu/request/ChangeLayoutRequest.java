package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeLayoutRequest extends ReaderBaseRequest {
    private Reader reader;
    private ChangeLayoutParameter parameter;
    private ImageReflowSettings settings;

    public ChangeLayoutRequest(Reader reader, ChangeLayoutParameter parameter, ImageReflowSettings settings) {
        this.reader = reader;
        this.parameter = parameter;
        this.settings = settings;
    }

    @Override
    public ChangeLayoutRequest call() throws Exception {
        updateSetting(reader);
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        reader.getReaderHelper().getReaderLayoutManager().setCurrentLayout(parameter.getLayout(), parameter.getNavigationArgs());
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        return this;
    }

    private void updateSetting(Reader reader) {
        if (settings != null) {
            settings.dev_width = reader.getReaderViewHelper().getContentWidth(reader.getReaderHelper().getContext());
            settings.dev_height = reader.getReaderViewHelper().getContentHeight(reader.getReaderHelper().getContext());
            settings.justification = 3;
            reader.getReaderHelper().getImageReflowManager().updateSettings(settings);
            reader.getReaderHelper().getImageReflowManager().notifySettingsUpdated();
        }
    }
}
