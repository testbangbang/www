package com.onyx.kreader.host.request;

import android.graphics.PointF;
import com.onyx.kreader.api.ReaderHitTestArgs;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class SelectWordRequest extends BaseRequest {

    private PointF start = new PointF();
    private PointF end = new PointF();
    private ReaderSelection selection;
    private String pageName;

    public SelectWordRequest(final String name, final PointF startPoint, final PointF endPoint) {
        start.set(startPoint.x, startPoint.y);
        end.set(endPoint.x, endPoint.y);
        pageName = name;
    }

    // check page at first. and then goto the location.
    public void execute(final Reader reader) throws Exception {
        ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
        PageInfo pageInfo = reader.getReaderLayoutManager().getPageManager().getPageInfo(pageName);
        ReaderHitTestArgs args = new ReaderHitTestArgs(pageName, pageInfo.getDisplayRect(), 0, start);
        selection = hitTestManager.selectWord(args, null);
    }

    public final ReaderSelection getSelection() {
        return selection;
    }

}
