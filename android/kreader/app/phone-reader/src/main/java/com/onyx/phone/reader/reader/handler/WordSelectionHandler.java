package com.onyx.phone.reader.reader.handler;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.request.SelectWordRequest;
import com.onyx.phone.reader.action.SelectWordAction;
import com.onyx.phone.reader.reader.data.ReaderDataHolder;

/**
 * Created by ming on 2017/4/13.
 */

public class WordSelectionHandler extends BaseHandler {

    private PointF highLightBeginTop = new PointF();
    private PointF highLightEndBottom = new PointF();
    private Point longPressPoint = new Point();

    public WordSelectionHandler(HandlerManager handlerManager) {
        super(handlerManager);
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
        longPressPoint.set((int) x2, (int) y2);
        highLightBeginTop.set(x2, y2);
    }

    @Override
    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        quiteWordSelection(readerDataHolder);
        return true;
    }

    private void quiteWordSelection(final ReaderDataHolder readerDataHolder) {
        getHandlerManager().resetToDefaultProvider();
        readerDataHolder.getSelectionManager().clearSelection();
        readerDataHolder.redrawPage();
    }

    @Override
    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                highlightAlongTouchMoved(readerDataHolder,x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    public void highlightAlongTouchMoved(ReaderDataHolder readerDataHolder, float x, float y) {
        selectText(readerDataHolder, longPressPoint.x, longPressPoint.y, x, y);
    }

    public void selectText(final ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
        PointF touchPoint = new PointF(x2, y2);
        final ReaderSelection selection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
        highLightEndBottom = new PointF(x2, y2);

        SelectWordAction.selectText(readerDataHolder, readerDataHolder.getCurrentPagePosition(), highLightBeginTop, highLightEndBottom, touchPoint, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onSelectWordFinished(readerDataHolder, (SelectWordRequest)request, e);
            }
        });
    }

    public void onSelectWordFinished(ReaderDataHolder readerDataHolder, SelectWordRequest request, Throwable e) {
        if (e != null) {
            return;
        }

        if (request.getReaderUserDataInfo().hasHighlightResult()) {
            ReaderSelection selection = request.getReaderUserDataInfo().getHighlightResult();
            readerDataHolder.getSelectionManager().setCurrentSelection(selection);
            readerDataHolder.onRenderRequestFinished(request, e);
        }
    }
}
