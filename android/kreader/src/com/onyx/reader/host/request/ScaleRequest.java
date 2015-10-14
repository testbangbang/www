package com.onyx.reader.host.request;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.math.EntryInfo;
import com.onyx.reader.host.math.EntryManager;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.plugins.adobe.AdobeDocumentPositionImpl;

import java.util.List;

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

    public void execute(final Reader reader) throws Exception {
        reader.getReaderLayoutManager().setScale(scale, x, y);
        setRenderBitmap(reader.getReaderHelper().getRenderBitmap());
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

    public void execute2(final Reader reader) throws Exception {
        EntryManager manager = new EntryManager();

        for(int pn = 0; pn < 5; ++pn) {
            ReaderDocumentPosition documentPosition = reader.getNavigator().getPositionByPageNumber(pn);
            RectF pageRect = reader.getDocument().getPageNaturalSize(documentPosition);
            EntryInfo entryInfo = new EntryInfo(pageRect.width(), pageRect.height());
            manager.add(documentPosition.save(), entryInfo);
        }
        manager.update();


        float width = reader.getViewOptions().getViewWidth();
        float height = reader.getViewOptions().getViewHeight();
        manager.setViewportRect(0, 0, width, height);

        manager.setScale(scale);
        manager.setViewport(x, y);

        clearBitmap(reader);
        List<EntryInfo> visiblePages = manager.updateVisiblePages();
        for(EntryInfo entryInfo : visiblePages) {
            ReaderDocumentPosition documentPosition = reader.getNavigator().createPositionFromString(entryInfo.getName());
            reader.getNavigator().gotoPosition(documentPosition);
            reader.getRenderer().setScale(manager.getActualScale());
            final RectF entryViewport = entryInfo.viewportInPage(manager.getViewportRect());
            reader.getRenderer().setViewport(entryViewport.left, entryViewport.top);
            final RectF visibleRect = entryInfo.visibleRectInViewport(manager.getViewportRect());
            renderToBitmap(reader, visibleRect);
        }

    }
}
