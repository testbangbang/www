package com.onyx.jdread.reader.request;

import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;

/**
 * Created by huxiaomao on 2017/12/27.
 */

public class NextScreenRequest extends ReaderBaseRequest {
    private Reader reader;
    private SettingInfo settingInfo;

    public NextScreenRequest(Reader reader,SettingInfo settingInfo) {
        this.reader = reader;
        this.settingInfo = settingInfo;
    }

    @Override
    public NextScreenRequest call() throws Exception {
        reader.getReaderHelper().nextScreen();
        updateSetting(reader);
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        saveReaderOptions(reader,settingInfo);
        return this;
    }
}
