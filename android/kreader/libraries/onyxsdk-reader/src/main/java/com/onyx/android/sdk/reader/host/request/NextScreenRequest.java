package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.math.PageOverlayMarker;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class NextScreenRequest extends BaseReaderRequest {

    private boolean noRender = false;

    public NextScreenRequest() {
    }

    public NextScreenRequest(boolean noRender) {
        this.noRender = noRender;
    }

    public void execute(final Reader reader) throws Exception {
        if (noRender) {
            reader.getReaderLayoutManager().nextScreen();
            return;
        }
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        PageOverlayMarker.saveCurrentPageAndViewport(reader);
        reader.getReaderLayoutManager().nextScreen();
        drawVisiblePages(reader);
        PageOverlayMarker.markLastViewportOverlayPointWhenNecessary(reader, getReaderViewInfo());
    }

}
