package com.onyx.kreader.ui.actions;

import android.content.IntentFilter;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.request.ScaleByRectRequest;
import com.onyx.kreader.ui.ReaderActivity;

import java.io.File;

/**
 * Created by zhuzeng on 5/20/16.
 */
public class ChangeScaleWithDeltaAction extends BaseAction {

    private float scaleDelta;

    public ChangeScaleWithDeltaAction(float delta) {
        scaleDelta = delta;
    }

    public void execute(final ReaderActivity readerActivity) {
        final RectF viewport = readerActivity.getReaderViewInfo().viewportInDoc;
        final RectF pos = new RectF();
        float offset = viewport.width() * scaleDelta;
        pos.set(viewport.left + offset,
                viewport.top + offset,
                viewport.right - offset,
                viewport.bottom - offset);
        scaleByRect(readerActivity, pos);
    }

    private void scaleByRect(final ReaderActivity readerActivity, final RectF rect) {
        final ScaleByRectRequest request = new ScaleByRectRequest(readerActivity.getCurrentPageName(), rect);
        readerActivity.submitRenderRequest(request);
    }

}
