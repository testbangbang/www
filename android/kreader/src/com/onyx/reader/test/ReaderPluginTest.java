package com.onyx.reader.test;

import android.graphics.Matrix;
import android.graphics.RectF;
import com.onyx.reader.api.*;

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
        ReaderDocument document = plugin.open("", null, null);
        ReaderDocumentMetadata metadata = defaultMetadata();
        document.readMetadata(metadata);

        ReaderBitmap readerBitmap = defaultBitmap();
        document.readCover(readerBitmap);

        ReaderDocumentTableOfContent toc = null;
        document.readTableOfContent(toc);
    }

    public void testPluginUsage() throws Exception {
        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null, null);
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
        ReaderDocument document = plugin.open("", null, null);
        ReaderBitmap readerBitmap = defaultBitmap();

        ReaderViewOptions viewOptions = defaultViewOptions();
        ReaderView readerView = document.createView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        ReaderSearchOptions searchOptions = defaultSearchOptions();
        ReaderSearchManager searchManager = readerView.getSearchManager();
        while (searchManager.searchNext(searchOptions)) {
            List<ReaderSelection> selections = searchManager.searchResults();
            ReaderDocumentPosition startPosition = selections.get(0).getStartPosition();
            ReaderDocumentPosition endPosition = selections.get(0).getEndPosition();
            navigator.gotoPosition(startPosition);
            navigator.gotoPosition(endPosition);
            renderer.draw(readerBitmap);
        }

        document.close();
    }


    /**
     * sub page navigation.
     * 1. goto position
     * 2. change scale
     * 3. change viewportInPage
     * @throws Exception
     */
    public void testSubPageNavigation() throws Exception {
        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null, null);
        ReaderBitmap readerBitmap = defaultBitmap();

        ReaderViewOptions viewOptions = defaultViewOptions();
        ReaderView readerView = document.createView(viewOptions);


        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        navigator.gotoPosition(navigator.getInitPosition());

        // change position and scale at first.
        float scale = 5.0f;
        int pn = 3;
        ReaderDocumentPosition position = navigator.getPositionByPageNumber(pn);
        navigator.gotoPosition(position);
        readerView.getRenderer().setScale(scale);

        // calculate the viewportInPage, according to original size, actual scale.
        RectF size = document.getPageNaturalSize(position);
        RectF pageDisplayRect = new RectF();
        RectF viewportRect = new RectF();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.mapRect(pageDisplayRect, size);
        readerView.getRenderer().setViewport(viewportRect.left, viewportRect.top);
        renderer.draw(readerBitmap);

        // move viewportInPage
        viewportRect.offset(100, 0);
        readerView.getRenderer().setViewport(viewportRect.left, viewportRect.top);
        renderer.draw(readerBitmap);

        document.close();
    }

    /**
     * position, scale and viewportInPage stack.
     * @throws Exception
     */
    public void testPrevNextView() throws Exception {
        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null, null);
        ReaderBitmap readerBitmap = defaultBitmap();

        ReaderViewOptions viewOptions = defaultViewOptions();
        ReaderView readerView = document.createView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        navigator.gotoPosition(navigator.getInitPosition());

        // change position and scale at first.
        float scale = 5.0f;
        int pn = 3;
        ReaderDocumentPosition position = navigator.getPositionByPageNumber(pn);
        navigator.gotoPosition(position);
        readerView.getRenderer().setScale(scale);

        // calculate the viewportInPage, according to original size, actual scale.
        RectF size = document.getPageNaturalSize(position);
        RectF pageDisplayRect = new RectF();
        RectF viewportRect = new RectF();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.mapRect(pageDisplayRect, size);


        // save current position, scale and viewportInPage.


        renderer.draw(readerBitmap);

        // move viewportInPage
        viewportRect.offset(100, 0);
        renderer.draw(readerBitmap);

        document.close();
    }


    /**
     * Test page layout. page layout
     * @throws Exception
     */
    public void testPageLayout() throws Exception {
        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null, null);
        ReaderBitmap readerBitmap = defaultBitmap();

        ReaderViewOptions viewOptions = defaultViewOptions();
        ReaderView readerView = document.createView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        navigator.gotoPosition(navigator.getInitPosition());

        // change position and scale at first.
        float scale = 5.0f;
        int pn = 3;
        ReaderDocumentPosition position = navigator.getPositionByPageNumber(pn);
        navigator.gotoPosition(position);
        readerView.getRenderer().setScale(scale);

        // calculate the viewportInPage, according to original size, actual scale.
        RectF size = document.getPageNaturalSize(position);
        RectF pageDisplayRect = new RectF();
        RectF viewportRect = new RectF();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.mapRect(pageDisplayRect, size);
        readerView.getRenderer().setScale(scale);
        readerView.getRenderer().setViewport(viewportRect.left, viewportRect.top);

        // save current position, scale and viewportInPage.


        renderer.draw(readerBitmap);

        // move viewportInPage
        viewportRect.offset(100, 0);
        readerView.getRenderer().setViewport(viewportRect.left, viewportRect.top);
        renderer.draw(readerBitmap);

        document.close();
    }

}
