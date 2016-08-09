package com.onyx.kreader.ui.actions;

import android.graphics.PointF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.host.request.SelectWordRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by Joy on 2016/6/3.
 */
public class SelectWordAction extends BaseAction {
    private String pageName;
    private PointF startPoint;
    private PointF endPoint;
    private boolean touchMoved;
    private OnSelectWordCallBack onSelectWordCallBack;

    public interface OnSelectWordCallBack{
        void onSelectWordFinished(ReaderDataHolder readerDataHolder,SelectWordRequest request, Throwable e,boolean touchMoved);
    }

    public SelectWordAction(final String pageName, final PointF startPoint, final PointF endPoint,final boolean touchMoved,OnSelectWordCallBack onSelectWordCallBack) {
        this.pageName = pageName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.touchMoved = touchMoved;
        this.onSelectWordCallBack = onSelectWordCallBack;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        final SelectWordRequest selectWordRequest = new SelectWordRequest(pageName, startPoint, endPoint);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), selectWordRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onSelectWordCallBack.onSelectWordFinished(readerDataHolder, selectWordRequest, e, touchMoved);
            }
        });
    }
}
