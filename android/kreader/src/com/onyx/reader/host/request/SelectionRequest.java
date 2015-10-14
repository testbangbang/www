package com.onyx.reader.host.request;

import android.graphics.PointF;
import com.onyx.reader.api.ReaderHitTestManager;
import com.onyx.reader.api.ReaderSelection;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

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
