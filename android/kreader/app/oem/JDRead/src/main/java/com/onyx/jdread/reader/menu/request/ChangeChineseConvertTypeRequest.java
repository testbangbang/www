package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.data.SettingInfo;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ChangeChineseConvertTypeRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderChineseConvertType convertType;
    private SettingInfo settingInfo;

    public ChangeChineseConvertTypeRequest(Reader reader, ReaderChineseConvertType convertType,final SettingInfo settingInfo) {
        this.reader = reader;
        this.convertType = convertType;
        this.settingInfo = settingInfo;
    }

    @Override
    public ChangeChineseConvertTypeRequest call() throws Exception {
        reader.getReaderHelper().getDocumentOptions().setChineseConvertType(convertType);
        reader.getReaderHelper().getRenderer().setChineseConvertType(convertType);
        reader.getReaderHelper().getBitmapCache().clear();
        reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(reader);
        saveReaderOptions(reader);
        saveStyleOptions(reader,settingInfo);
        return this;
    }
}
