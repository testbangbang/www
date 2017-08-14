package com.onyx.kreader.tests;

import android.graphics.Matrix;
import android.graphics.RectF;
import com.onyx.android.sdk.reader.api.*;

import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 * sample code to use plugin
 */
public class SampleCode {

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
        ReaderView readerView = document.getView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        String initPosition = navigator.getInitPosition();
        navigator.gotoPosition(initPosition);
        String current = initPosition;
        renderer.draw(null, -1, readerBitmap);
        while ((current = navigator.nextScreen(current)) != null) {
            renderer.draw(null,  -1, readerBitmap);
        }
        while ((current = navigator.prevScreen(current)) != null) {
            renderer.draw(null,  -1, readerBitmap);
        }
        document.close();
    }

    public void testViewSearch() throws Exception {
        ReaderPlugin plugin = getPlugin();
        ReaderDocument document = plugin.open("", null, null);
        ReaderBitmap readerBitmap = defaultBitmap();

        ReaderViewOptions viewOptions = defaultViewOptions();
        ReaderView readerView = document.getView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        ReaderSearchOptions searchOptions = defaultSearchOptions();
        ReaderSearchManager searchManager = readerView.getSearchManager();
        while (searchManager.searchNext(searchOptions)) {
            List<ReaderSelection> selections = searchManager.searchResults();
            String startPosition = selections.get(0).getStartPosition();
            String endPosition = selections.get(0).getEndPosition();
            navigator.gotoPosition(startPosition);
            navigator.gotoPosition(endPosition);
            renderer.draw(startPosition, -1, readerBitmap);
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
        ReaderView readerView = document.getView(viewOptions);


        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        navigator.gotoPosition(navigator.getInitPosition());

        // change position and scale at first.
        float scale = 5.0f;
        int pn = 3;
        String position = navigator.getPositionByPageNumber(pn);
        navigator.gotoPosition(position);

        // calculate the viewportInPage, according to original size, actual scale.
        RectF size = document.getPageOriginSize(position);
        RectF pageDisplayRect = new RectF();
        RectF viewportRect = new RectF();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.mapRect(pageDisplayRect, size);
        renderer.draw(position,  -1, readerBitmap);

        // move viewportInPage
        viewportRect.offset(100, 0);
        renderer.draw(position,  -1, readerBitmap);

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
        ReaderView readerView = document.getView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        navigator.gotoPosition(navigator.getInitPosition());

        // change position and scale at first.
        float scale = 5.0f;
        int pn = 3;
        String position = navigator.getPositionByPageNumber(pn);
        navigator.gotoPosition(position);

        // calculate the viewportInPage, according to original size, actual scale.
        RectF size = document.getPageOriginSize(position);
        RectF pageDisplayRect = new RectF();
        RectF viewportRect = new RectF();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.mapRect(pageDisplayRect, size);


        // asString current position, scale and viewportInPage.


        renderer.draw(position,  -1, readerBitmap);

        // move viewportInPage
        viewportRect.offset(100, 0);
        renderer.draw(position,  -1, readerBitmap);

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
        ReaderView readerView = document.getView(viewOptions);

        ReaderNavigator navigator = readerView.getNavigator();
        ReaderRenderer renderer = readerView.getRenderer();
        navigator.gotoPosition(navigator.getInitPosition());

        // change position and scale at first.
        float scale = 5.0f;
        int pn = 3;
        String position = navigator.getPositionByPageNumber(pn);
        navigator.gotoPosition(position);

        // calculate the viewportInPage, according to original size, actual scale.
        RectF size = document.getPageOriginSize(position);
        RectF pageDisplayRect = new RectF();
        RectF viewportRect = new RectF();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.mapRect(pageDisplayRect, size);

        // asString current position, scale and viewportInPage.
        renderer.draw(position,  -1, readerBitmap);

        // move viewportInPage
        viewportRect.offset(100, 0);
        renderer.draw(position,  -1, readerBitmap);

        document.close();
    }
}
