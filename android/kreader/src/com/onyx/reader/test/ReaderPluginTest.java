package com.onyx.reader.test;

import com.onyx.reader.plugin.*;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public class ReaderPluginTest {

    public ReaderPlugin getPlugin() {
        return null;
    }

    public ReaderViewOptions defaultViewOptions() {
        return null;
    }

    public ReaderBitmap defaultBitmap() {
        return null;
    }

    public void testPluginUsage() throws Exception {

        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null);
        ReaderBitmap readerBitmap = defaultBitmap();

        ReaderViewOptions viewOptions = defaultViewOptions();
        ReaderView readerView = document.createView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        ReaderDocumentPosition initPosition = navigator.getInitPosition();
        navigator.gotoPosition(initPosition);
        renderer.draw(readerBitmap);
        while (navigator.nextScreen()) {
            renderer.draw(readerBitmap);
        }
        document.close();
    }





}
