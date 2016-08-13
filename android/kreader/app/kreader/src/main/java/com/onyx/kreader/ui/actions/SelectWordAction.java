package com.onyx.kreader.ui.actions;

import android.graphics.PointF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.kreader.host.request.SelectWordRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.PopupSelectionMenu;

/**
 * Created by Joy on 2016/6/3.
 */
public class SelectWordAction extends BaseAction {
    private PopupSelectionMenu.SelectionType selectionType;
    private String pageName;
    private PointF startPoint;
    private PointF endPoint;
    private boolean touchMoving;
    private OnSelectWordCallBack onSelectWordCallBack;

    public interface OnSelectWordCallBack{
        void onSelectWordFinished(ReaderDataHolder readerDataHolder, SelectWordRequest request, Throwable e, boolean touchMoving, PopupSelectionMenu.SelectionType selectionType);
    }

    public SelectWordAction(final String pageName, final PointF startPoint, final PointF endPoint, final boolean touchMoving, PopupSelectionMenu.SelectionType selectionType, OnSelectWordCallBack onSelectWordCallBack) {
        this.pageName = pageName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.touchMoving = touchMoving;
        this.selectionType = selectionType;
        this.onSelectWordCallBack = onSelectWordCallBack;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        SelectWordRequest request = new SelectWordRequest(pageName, startPoint, endPoint);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), request, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onSelectWordCallBack.onSelectWordFinished(readerDataHolder,(SelectWordRequest)request, e, touchMoving, selectionType);
            }
        });
    }
}
