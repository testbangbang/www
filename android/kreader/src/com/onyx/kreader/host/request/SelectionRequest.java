package com.onyx.kreader.host.request;

import android.graphics.PointF;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/13/15.
 */
public class SelectionRequest extends BaseRequest {

    private PointF start = new PointF();
    private PointF end = new PointF();
    private ReaderSelection selection;

    public SelectionRequest(final PointF s, final PointF e) {
        start.set(s.x, s.y);
        end.set(e.x, e.y);
    }

    // check page at first. and then goto the location.
    public void execute(final Reader reader) throws Exception {
        ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
        selection = hitTestManager.select(start, end);
    }

    public final ReaderSelection getSelection() {
        return selection;
    }


}
