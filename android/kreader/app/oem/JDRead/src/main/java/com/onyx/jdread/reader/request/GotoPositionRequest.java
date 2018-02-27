package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.SettingInfo;

/**
 * Created by huxiaomao on 2018/1/30.
 */

public class GotoPositionRequest extends ReaderBaseRequest {
    private Reader reader;
    private String persistentPosition;
    private int page;
    private boolean isDrawPage = true;
    private SettingInfo settingInfo;

    public GotoPositionRequest(Reader reader, int page, boolean isDrawPage,SettingInfo settingInfo) {
        this.reader = reader;
        this.page = page;
        this.isDrawPage = isDrawPage;
        this.settingInfo = settingInfo;
    }

    public GotoPositionRequest(Reader reader, int page,SettingInfo settingInfo) {
        this.reader = reader;
        this.page = page;
        this.settingInfo = settingInfo;
    }

    public GotoPositionRequest(Reader reader, String persistentPosition,SettingInfo settingInfo) {
        this.reader = reader;
        this.persistentPosition = persistentPosition;
        this.settingInfo = settingInfo;
    }

    public GotoPositionRequest(Reader reader, String persistentPosition, boolean isDrawPage,SettingInfo settingInfo) {
        this.reader = reader;
        this.persistentPosition = persistentPosition;
        this.isDrawPage = isDrawPage;
        this.settingInfo = settingInfo;
    }

    @Override
    public GotoPositionRequest call() throws Exception {
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        String documentPosition;
        if (StringUtils.isNotBlank(persistentPosition)) {
            documentPosition = persistentPosition;
        } else {
            documentPosition = reader.getReaderHelper().getNavigator().getPositionByPageNumber(page);
        }
        if (!reader.getReaderHelper().getReaderLayoutManager().gotoPosition(documentPosition)) {
            throw ReaderException.outOfRange();
        }
        if (isDrawPage) {
            reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo());
        }
        updateSetting(reader);
        saveReaderOptions(reader,settingInfo);
        return this;
    }
}
