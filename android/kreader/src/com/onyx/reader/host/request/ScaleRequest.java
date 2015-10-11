package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleRequest extends BaseRequest {

    private float scale;
    private float x, y;

    public ScaleRequest(float s, float viewportX, float viewportY) {
        scale = s;
        x = viewportX;
        y = viewportY;
    }

    public void execute2(final Reader reader) throws Exception {
        reader.getReaderHelper().renderer.setScale(scale);
        reader.getReaderHelper().renderer.setViewport(x, y);
        renderToBitmap(reader);
    }

    public void execute(final Reader reader) throws Exception {
        EntryManager manager = new EntryManager();

        ReaderDocumentPosition documentPosition = reader.getReaderHelper().navigator.getVisibleBeginningPosition();
        RectF pageRect = reader.getReaderHelper().document.getPageNaturalSize(documentPosition);
        EntryInfo entryInfo = new EntryInfo(pageRect.width(), pageRect.height());
        manager.add(documentPosition.save(), entryInfo);
        manager.update();


        float width = reader.getReaderHelper().viewOptions.getViewWidth();
        float height = reader.getReaderHelper().viewOptions.getViewHeight();
        manager.setViewportRect(0, 0, width, height);

        manager.setScale(scale);
        manager.setViewport(x, y);
        reader.getReaderHelper().renderer.setScale(manager.getActualScale());
        reader.getReaderHelper().renderer.setViewport(manager.getViewportRect().left, manager.getViewportRect().top);
        renderToBitmap(reader);
    }
}
