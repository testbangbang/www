package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class StopSideNodeRequest extends BaseReaderRequest {

    private int newWidth, newHeight;

    public StopSideNodeRequest(int nw, int nh) {
        newWidth = nw;
        newHeight = nh;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);

        PositionSnapshot positionSnapshot = reader.getReaderLayoutManager().getSideNotePositionSnapshot();
        positionSnapshot.pagePosition = reader.getReaderLayoutManager().getCurrentPagePosition();
        reader.getReaderLayoutManager().restoreSideNoteSnapshot(positionSnapshot);
        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);

        drawVisiblePages(reader);
    }
}
