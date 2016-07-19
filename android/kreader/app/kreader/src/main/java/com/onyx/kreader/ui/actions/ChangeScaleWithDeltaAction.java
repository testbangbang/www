package com.onyx.kreader.ui.actions;

import android.graphics.RectF;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.ScaleByRectRequest;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 5/20/16.
 */
public class ChangeScaleWithDeltaAction extends BaseAction {

    private float scaleDelta;

    public ChangeScaleWithDeltaAction(float delta) {
        scaleDelta = delta;
    }

    public void execute(final ReaderActivity readerActivity) {
        if (scaleDelta < 0 && !canScaleDown(readerActivity)) {
            return;
        }

        final RectF viewport = readerActivity.getReaderViewInfo().viewportInDoc;
        final RectF pos = new RectF();
        float offset = viewport.width() * scaleDelta;
        pos.set(viewport.left + offset,
                viewport.top + offset,
                viewport.right - offset,
                viewport.bottom - offset);
        scaleByRect(readerActivity, pos);
    }

    private boolean canScaleDown(final ReaderActivity readerActivity) {
        final float toPageScale = PageUtils.scaleToPage(readerActivity.getFirstPageInfo().getOriginWidth(), readerActivity.getFirstPageInfo().getOriginHeight(), readerActivity.getReader().getViewOptions().getViewWidth(), readerActivity.getReader().getViewOptions().getViewHeight());
        return readerActivity.getReaderViewInfo().getFirstVisiblePage().getActualScale() > toPageScale;
    }

    private void scaleByRect(final ReaderActivity readerActivity, final RectF rect) {
        final ScaleByRectRequest request = new ScaleByRectRequest(readerActivity.getCurrentPageName(), rect);
        readerActivity.submitRequest(request);
    }

}
