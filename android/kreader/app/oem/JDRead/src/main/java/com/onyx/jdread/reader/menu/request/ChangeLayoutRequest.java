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
    private String pageName;

    public ChangeLayoutRequest(Reader reader, ChangeLayoutParameter parameter, ImageReflowSettings settings,String pageName) {
        this.reader = reader;
        this.parameter = parameter;
        this.settings = settings;
        this.pageName = pageName;
    }

    @Override
    public ChangeLayoutRequest call() throws Exception {
        updateDefaultSettingValue(reader);
        reader.getReaderHelper().getBitmapCache().clear();
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        reader.getReaderHelper().getReaderLayoutManager().setCurrentLayout(parameter.getLayout(), parameter.getNavigationArgs());
        reader.getReaderHelper().getReaderLayoutManager().scaleToPage(pageName);
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());

        updateSetting(reader);
        return this;
    }

    public void updateDefaultSettingValue(Reader reader) {
        if (settings != null) {
            settings.dev_width = reader.getReaderViewHelper().getContentWidth();
            settings.dev_height = reader.getReaderViewHelper().getContentHeight();
            settings.justification = 3;
            reader.getReaderHelper().getImageReflowManager().updateSettings(settings);
            reader.getReaderHelper().getImageReflowManager().notifySettingsUpdated();
        }
    }
}
