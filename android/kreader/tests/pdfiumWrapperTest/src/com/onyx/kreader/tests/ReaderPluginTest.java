package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.api.*;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.impl.ReaderViewOptionsImpl;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.plugins.pdfium.PdfiumReaderPlugin;
import com.onyx.kreader.test.ReaderTestActivity;
import com.onyx.kreader.utils.BitmapUtils;
import com.onyx.kreader.utils.RectUtils;
import com.onyx.kreader.utils.TestUtils;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderPluginTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderPluginTest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }



    public void testPluginUsage() throws Exception {
        ReaderPlugin plugin = new PdfiumReaderPlugin(getActivity());
        ReaderDocument document = plugin.open("/mnt/sdcard/Books/a.pdf", null, null);
        assertNotNull(document);
        ReaderViewOptionsImpl viewOptions = new ReaderViewOptionsImpl(1024, 768);
        ReaderView readerView = document.createView(viewOptions);
        assertNotNull(readerView);
        ReaderBitmap readerBitmap = new ReaderBitmapImpl(viewOptions.getViewWidth(), viewOptions.getViewHeight(), Bitmap.Config.ARGB_8888);
        ReaderNavigator navigator = readerView.getNavigator();
        assertNotNull(navigator);
        ReaderRenderer renderer = readerView.getRenderer();
        assertNotNull(renderer);
        String initPosition = navigator.getInitPosition();
        assertNotNull(initPosition);
        assertTrue(renderer.draw(initPosition, -1, readerBitmap));
        document.close();
    }


    public void testPluginRendering() throws Exception {
        ReaderPlugin plugin = new PdfiumReaderPlugin(getActivity());
        ReaderDocument document = plugin.open("/mnt/sdcard/Books/a.pdf", null, null);
        assertNotNull(document);

        ReaderViewOptionsImpl viewOptions = new ReaderViewOptionsImpl(TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));
        ReaderView readerView = document.createView(viewOptions);
        assertNotNull(readerView);
        ReaderBitmap readerBitmap = new ReaderBitmapImpl(viewOptions.getViewWidth(), viewOptions.getViewHeight(), Bitmap.Config.ARGB_8888);
        ReaderNavigator navigator = readerView.getNavigator();
        assertNotNull(navigator);
        ReaderRenderer renderer = readerView.getRenderer();
        assertNotNull(renderer);
        String initPosition = navigator.getInitPosition();
        assertNotNull(initPosition);


        PageManager pageManager = new PageManager();
        RectF viewport = new RectF(0, 0, viewOptions.getViewWidth(), viewOptions.getViewHeight());
        pageManager.setViewportRect(viewport.left, viewport.top, viewport.width(), viewport.height());

        RectF size = document.getPageOriginSize(initPosition);
        PageInfo pageInfo = new PageInfo(initPosition, size.width(), size.height());
        pageManager.add(pageInfo);
        pageManager.scaleToPage();
        PageInfo result = pageManager.getFirstVisiblePage();
        Rect displayRect = RectUtils.toRect(result.getDisplayRect());
        assertTrue(renderer.draw(initPosition, result.getActualScale(), readerBitmap, displayRect.left, displayRect.top,
                displayRect.width(), displayRect.height()));
        BitmapUtils.saveBitmap(readerBitmap.getBitmap(), "/mnt/sdcard/1.png");
        document.close();
    }

}
