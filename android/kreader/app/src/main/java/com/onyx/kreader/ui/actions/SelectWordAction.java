package com.onyx.kreader.ui.actions;

import android.graphics.PointF;
import com.onyx.kreader.common.BaseCallback;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.request.SelectWordRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by Joy on 2016/6/3.
 */
public class SelectWordAction extends BaseAction {
    private String pageName;
    private PointF startPoint;
    private PointF endPoint;

    public SelectWordAction(final String pageName, final PointF startPoint, final PointF endPoint) {
        this.pageName = pageName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    @Override
    public void execute(final ReaderActivity readerActivity) {
        SelectWordRequest request = new SelectWordRequest(pageName, startPoint, endPoint);
        readerActivity.getReader().submitRequest(readerActivity, request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readerActivity.onSelectWordFinished((SelectWordRequest)request, e);
            }
        });
    }
}
