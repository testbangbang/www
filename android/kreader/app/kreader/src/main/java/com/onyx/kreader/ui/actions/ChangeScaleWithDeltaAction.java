package com.onyx.kreader.ui.actions;

import android.graphics.RectF;
import android.widget.Toast;

import com.onyx.kreader.R;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.ScaleByRectRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/20/16.
 */
public class ChangeScaleWithDeltaAction extends BaseAction {

    private float scaleDelta;

    public ChangeScaleWithDeltaAction(float delta) {
        scaleDelta = delta;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        if (scaleDelta < 0 && !canScaleDown(readerDataHolder)) {
            Toast.makeText(readerDataHolder.getContext(),
                    R.string.min_scroll_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        final RectF viewport = readerDataHolder.getReaderViewInfo().viewportInDoc;
        final RectF pos = new RectF();
        float offset = viewport.width() * scaleDelta;
        pos.set(viewport.left + offset,
                viewport.top + offset,
                viewport.right - offset,
                viewport.bottom - offset);
        scaleByRect(readerDataHolder, pos);
    }

    private boolean canScaleDown(final ReaderDataHolder readerDataHolder) {
        final float toPageScale = PageUtils.scaleToPage(readerDataHolder.getFirstPageInfo().getOriginWidth(), readerDataHolder.getFirstPageInfo().getOriginHeight(), readerDataHolder.getReader().getViewOptions().getViewWidth(), readerDataHolder.getReader().getViewOptions().getViewHeight());
        return readerDataHolder.getReaderViewInfo().getFirstVisiblePage().getActualScale() > toPageScale;
    }

    private void scaleByRect(final ReaderDataHolder readerDataHolder, final RectF rect) {
        final ScaleByRectRequest request = new ScaleByRectRequest(readerDataHolder.getCurrentPageName(), rect);
        readerDataHolder.submitRenderRequest(request);
    }

}
