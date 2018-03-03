package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class SettingTextStyleRequest extends ReaderBaseRequest {
    private ReaderTextStyle inputStyle;
    private SettingInfo settingInfo;

    public SettingTextStyleRequest(Reader reader,ReaderTextStyle inputStyle,SettingInfo settingInfo) {
        super(reader);
        this.inputStyle = inputStyle;
        this.settingInfo = settingInfo;
    }

    @Override
    public SettingTextStyleRequest call() throws Exception {

        getReader().getReaderHelper().getTextStyleManager().setStyle(inputStyle);
        getReader().getReaderHelper().getBitmapCache().clear();
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());

        updateSetting(getReader());
        saveReaderOptions(getReader());
        saveStyleOptions(getReader(),settingInfo);
        return this;
    }
}
