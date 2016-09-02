package com.onyx.kreader.host.request;

import android.graphics.PointF;
import com.onyx.kreader.api.ReaderHitTestArgs;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/13/15.
 */
public class AnnotationRequest extends BaseReaderRequest {

    private String pageName;
    private PointF start = new PointF();
    private PointF end = new PointF();
    private ReaderSelection selection;

    public AnnotationRequest(final String name, final PointF s, final PointF e) {
        start.set(s.x, s.y);
        end.set(e.x, e.y);
        pageName = name;
    }

    // check page at first. and then goto the location.
    public void execute(final Reader reader) throws Exception {
        ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
        PageInfo pageInfo = reader.getReaderLayoutManager().getPageManager().getPageInfo(pageName);
        ReaderHitTestArgs startArgs = new ReaderHitTestArgs(pageName, pageInfo.getDisplayRect(), 0, start);
        ReaderHitTestArgs endArgs = new ReaderHitTestArgs(pageName, pageInfo.getDisplayRect(), 0, end);
        selection = hitTestManager.select(startArgs, endArgs, false);
    }

    public final ReaderSelection getSelection() {
        return selection;
    }


}
