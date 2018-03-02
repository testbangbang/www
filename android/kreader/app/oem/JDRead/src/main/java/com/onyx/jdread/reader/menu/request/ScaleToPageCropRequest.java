package com.onyx.jdread.reader.menu.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.request.ReaderBaseRequest;

/**
 * Created by huxiaomao on 2017/12/30.
 */

public class ScaleToPageCropRequest extends ReaderBaseRequest {
    private ReaderViewInfo readerViewInfo;

    public ScaleToPageCropRequest(Reader reader, ReaderViewInfo readerViewInfo) {
        super(reader);
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public ScaleToPageCropRequest call() throws Exception {
        String pageName = readerViewInfo.getFirstVisiblePage().getName();
        getReader().getReaderHelper().getReaderLayoutManager().setSavePosition(true);
        getReader().getReaderHelper().getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        getReader().getReaderHelper().getReaderLayoutManager().scaleToPageContent(pageName);
        getReader().getReaderViewHelper().updatePageView(getReader(), getReaderUserDataInfo(), getReaderViewInfo());
        updateSetting(getReader());
        return this;
    }
}
