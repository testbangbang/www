package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeChineseConvertTypeRequest extends ReaderBaseRequest {
    private ReaderChineseConvertType convertType;
    private SettingInfo settingInfo;

    public ChangeChineseConvertTypeRequest(Reader reader, ReaderChineseConvertType convertType,final SettingInfo settingInfo) {
        super(reader);
        this.convertType = convertType;
        this.settingInfo = settingInfo;
    }

    @Override
    public ChangeChineseConvertTypeRequest call() throws Exception {
        getReader().getReaderHelper().getDocumentOptions().setChineseConvertType(convertType);
        getReader().getReaderHelper().getRenderer().setChineseConvertType(convertType);
        getReader().getReaderHelper().clearBitmapCache();
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(getReader());
        saveReaderOptions(getReader());
        saveStyleOptions(getReader(),settingInfo);
        return this;
    }
}
