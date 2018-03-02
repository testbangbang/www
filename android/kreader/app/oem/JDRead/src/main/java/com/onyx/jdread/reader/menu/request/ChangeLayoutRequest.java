package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeLayoutRequest extends ReaderBaseRequest {
    private ChangeLayoutParameter parameter;
    private ImageReflowSettings settings;
    private String pageName;

    public ChangeLayoutRequest(Reader reader, ChangeLayoutParameter parameter, ImageReflowSettings settings,String pageName) {
        super(reader);
        this.parameter = parameter;
        this.settings = settings;
        this.pageName = pageName;
    }

    @Override
    public ChangeLayoutRequest call() throws Exception {
        updateDefaultSettingValue(getReader());
        getReader().getReaderHelper().getBitmapCache().clear();
        getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        getReader().getReaderHelper().getReaderLayoutManager().setCurrentLayout(parameter.getLayout(), parameter.getNavigationArgs());
        getReader().getReaderHelper().getReaderLayoutManager().scaleToPage(pageName);
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());

        updateSetting(getReader());
        return this;
    }

    public void updateDefaultSettingValue(Reader reader) {
        if (settings != null) {
            settings.dev_width = reader.getReaderViewHelper().getContentWidth();
            int readerBottomStateBarHeight = ResManager.getDimens(R.dimen.reader_content_view_bottom_state_bar_height);
            settings.dev_height = reader.getReaderViewHelper().getContentHeight() - readerBottomStateBarHeight;
            settings.justification = 3;
            reader.getReaderHelper().getImageReflowManager().updateSettings(settings);
            reader.getReaderHelper().getImageReflowManager().notifySettingsUpdated();
        }
    }
}
