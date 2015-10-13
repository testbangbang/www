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

    public void execute2(final Reader reader) throws Exception {
        reader.getReaderHelper().renderer.setScale(scale);
        reader.getReaderHelper().renderer.setViewport(x, y);
        renderToBitmap(reader);
    }

    public void execute(final Reader reader) throws Exception {
        EntryManager manager = new EntryManager();

        for(int pn = 0; pn < 5; ++pn) {
            AdobeDocumentPositionImpl documentPosition = new AdobeDocumentPositionImpl(pn);
            RectF pageRect = reader.getReaderHelper().document.getPageNaturalSize(documentPosition);
            EntryInfo entryInfo = new EntryInfo(pageRect.width(), pageRect.height());
            manager.add(documentPosition.save(), entryInfo);
        }
        manager.update();


        float width = reader.getReaderHelper().viewOptions.getViewWidth();
        float height = reader.getReaderHelper().viewOptions.getViewHeight();
        manager.setViewportRect(0, 0, width, height);

        manager.setScale(scale);
        manager.setViewport(x, y);

        clearBitmap(reader);
        List<EntryInfo> visiblePages = manager.updateVisiblePages();
        boolean single = true;
        for(EntryInfo entryInfo : visiblePages) {
            ReaderDocumentPosition documentPosition = reader.getReaderHelper().navigator.getPositionByPageName(entryInfo.getName());
            reader.getReaderHelper().navigator.gotoPosition(documentPosition);
            reader.getReaderHelper().renderer.setScale(manager.getActualScale());
            final RectF entryViewport = entryInfo.viewportInPage(manager.getViewportRect());
            reader.getReaderHelper().renderer.setViewport(entryViewport.left, entryViewport.top);
            final RectF visibleRect = entryInfo.visibleRectInViewport(manager.getViewportRect());
            renderToBitmap(reader, visibleRect);
        }

    }
}
