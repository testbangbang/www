package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ScaleToPageCropRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderViewInfo readerViewInfo;

    public ScaleToPageCropRequest(Reader reader, ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
        this.reader = reader;
    }

    @Override
    public ScaleToPageCropRequest call() throws Exception {
        String pageName = readerViewInfo.getFirstVisiblePage().getName();
        reader.getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        reader.getReaderHelper().getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        reader.getReaderHelper().getReaderLayoutManager().scaleToPageContent(pageName);
        reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(), getReaderViewInfo());
        updateSetting(reader);
        return this;
    }
}
