package com.onyx.android.sdk.reader.tests;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Pair;

import com.neverland.engbook.bookobj.AlBookEng;
import com.neverland.engbook.forpublic.AlBookProperties;
import com.neverland.engbook.forpublic.AlOneContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry;
import com.onyx.android.sdk.reader.api.ReaderDrmManager;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderViewOptionsImpl;
import com.onyx.android.sdk.reader.plugins.alreader.AlReaderPlugin;
import com.onyx.android.sdk.reader.plugins.alreader.AlReaderWrapper;
import com.onyx.android.sdk.utils.Debug;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by joy on 12/16/16.
 */

public class ReaderPluginAlReaderEpubTest extends ApplicationTestCase<Application> {

    public ReaderPluginAlReaderEpubTest() {
        super(Application.class);
    }

    public void testDrmOpenFail() throws ReaderException {
        AlReaderPlugin plugin = new AlReaderPlugin(getContext(), new ReaderPluginOptionsImpl());
        plugin.activateDeviceDRM(null, null);
        assertNull(plugin.open("/sdcard/Books/enc.epub", new ReaderDocumentOptionsImpl(null, null),
                new ReaderPluginOptionsImpl()));
    }

    public void testDrmOpenSuccess() throws ReaderException {
        DrmCertificateFactory factory = new DrmCertificateFactory(getContext());

        AlReaderPlugin plugin = new AlReaderPlugin(getContext(), new ReaderPluginOptionsImpl());
        plugin.activateDeviceDRM(factory.getDeviceId(), factory.getDrmCertificate());
        assertNotNull(plugin.open("/sdcard/Books/enc.epub", new ReaderDocumentOptionsImpl(null, null),
                new ReaderPluginOptionsImpl()));
        plugin.close();
    }

    public void testNonDrmOpen() throws ReaderException {
        AlReaderPlugin plugin = new AlReaderPlugin(getContext(), new ReaderPluginOptionsImpl());
        assertNotNull(plugin.open("/sdcard/Books/xxx.epub", new ReaderDocumentOptionsImpl(null, null),
                new ReaderPluginOptionsImpl()));
        plugin.close();
    }

    public void testDrmContent() throws ReaderException {
        AlReaderPlugin plugin = new AlReaderPlugin(getContext(), new ReaderPluginOptionsImpl());
        plugin.open("/sdcard/Books/xxx.epub", new ReaderDocumentOptionsImpl(null, null),
                new ReaderPluginOptionsImpl());
        plugin.getView(new ReaderViewOptionsImpl(1024, 758));

        int pageCount = plugin.getTotalPage();
        Debug.e("page count: " + pageCount);

        StringBuilder sb = new StringBuilder();
        while (!plugin.isLastPage()) {
            Debug.e("position: " +  plugin.getScreenStartPosition() + ", texts: " + plugin.getPageText(plugin.getScreenStartPosition()));
            sb.append(plugin.getPageText(plugin.getScreenStartPosition()));
            plugin.nextScreen(plugin.getScreenStartPosition());
        }


        plugin.close();

        plugin = new AlReaderPlugin(getContext(), new ReaderPluginOptionsImpl());

        DrmCertificateFactory factory = new DrmCertificateFactory(getContext());
        plugin.activateDeviceDRM(factory.getDeviceId(), factory.getDrmCertificate());
        plugin.open("/sdcard/Books/enc.epub", new ReaderDocumentOptionsImpl(null, null),
                new ReaderPluginOptionsImpl());
        plugin.getView(new ReaderViewOptionsImpl(1024, 758));

        assertEquals(pageCount, plugin.getTotalPage());
        StringBuilder sb2 = new StringBuilder();
        while (!plugin.isLastPage()) {
            Debug.e("position: " +  plugin.getScreenStartPosition() + ", texts: " + plugin.getPageText(plugin.getScreenStartPosition()));
            sb2.append(plugin.getPageText(plugin.getScreenStartPosition()));
            plugin.nextScreen(plugin.getScreenStartPosition());
        }

        assertEquals(sb.toString(), sb2.toString());

    }
}
