package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.api.*;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.impl.ReaderViewOptionsImpl;
import com.onyx.kreader.host.wrapper.Reader;
import com.onyx.kreader.plugins.pdfium.PdfiumReaderPlugin;
import com.onyx.kreader.test.ReaderTestActivity;

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
        ReaderPagePosition initPosition = navigator.getInitPosition();
        assertNotNull(initPosition);
        assertTrue(renderer.draw(initPosition, -1, readerBitmap));
        document.close();
    }


}
