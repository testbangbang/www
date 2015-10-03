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

    public ReaderDocumentMetadata defaultMetadata() {
        return null;
    }

    public ReaderSearchOptions defaultSearchOptions() {
        return null;
    }

    public void testPlugin() throws Exception {
        ReaderPlugin plugin = getPlugin();
        assert(plugin != null);
        assert(plugin.displayName() != null);
        assert(plugin.supportedFileList() != null);
        if (plugin.supportDrm()) {
            assert(plugin.createDrmManager() != null);
        }
    }

    public void testDocument() throws Exception {
        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null);
        ReaderDocumentMetadata metadata = defaultMetadata();
        document.readMetadata(metadata);

        ReaderBitmap readerBitmap = defaultBitmap();
        document.readCover(readerBitmap);

        ReaderDocumentTableOfContent toc = null;
        document.readTableOfContent(toc);
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
        while (navigator.prevScreen()) {
            renderer.draw(readerBitmap);
        }
        document.close();
    }

    public void testViewSearch() throws Exception {
        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null);
        ReaderBitmap readerBitmap = defaultBitmap();

        ReaderViewOptions viewOptions = defaultViewOptions();
        ReaderView readerView = document.createView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        ReaderSearchOptions searchOptions = defaultSearchOptions();
        ReaderSearchManager searchManager = readerView.getSearchManager();
        while (searchManager.searchNext(searchOptions)) {
            List<ReaderTextSelection> selections = searchManager.searchResults();
            ReaderDocumentPosition startPosition = selections.get(0).getStartPosition();
            ReaderDocumentPosition endPosition = selections.get(0).getEndPosition();
            navigator.gotoPosition(startPosition);
            navigator.gotoPosition(endPosition);
            renderer.draw(readerBitmap);
        }

        document.close();
    }




}
