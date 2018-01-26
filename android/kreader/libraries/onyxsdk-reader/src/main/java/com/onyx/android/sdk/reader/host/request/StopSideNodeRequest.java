package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class StopSideNodeRequest extends BaseReaderRequest {

    private int newWidth, newHeight;
    private PositionSnapshot positionSnapshot;

    public StopSideNodeRequest(int nw, int nh, PositionSnapshot positionSnapshot) {
        newWidth = nw;
        newHeight = nh;
        this.positionSnapshot = positionSnapshot;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().restoreSnapshot(positionSnapshot);
        reader.getReaderLayoutManager().getPageManager().restoreScale();
        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);

        drawVisiblePages(reader);
    }
}
