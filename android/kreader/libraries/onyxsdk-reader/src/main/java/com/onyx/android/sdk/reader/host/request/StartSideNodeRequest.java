package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.math.PositionSnapshot;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class StartSideNodeRequest extends BaseReaderRequest {

    private int newWidth, newHeight;

    public PositionSnapshot positionSnapshot;

    public StartSideNodeRequest(int nw, int nh) {
        newWidth = nw / 2;
        newHeight = nh;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);

        positionSnapshot = reader.getReaderLayoutManager().getCurrentLayoutProvider().saveSnapshot();

        reader.getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);
        reader.getReaderLayoutManager().scaleToPage(reader.getReaderLayoutManager().getCurrentPageName());

        drawVisiblePages(reader);
    }
}
