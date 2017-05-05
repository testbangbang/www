package com.onyx.android.sdk.reader.tests;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.android.sdk.reader.api.*;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/22/16.
 */
public class FakeReader implements ReaderDocument,
        ReaderView,
        ReaderRendererFeatures,
        ReaderNavigator,
        ReaderTextStyleManager,
        ReaderViewOptions {

    private Rect viewport;
    private List<PageInfo> pageInfoList = new ArrayList<PageInfo>();

    public boolean open() {
        viewport = new Rect(0, 0, TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));
        randPageList();
        return true;
    }

    private final List<PageInfo> randPageList() {
        pageInfoList.clear();
        int count = TestUtils.randInt(10, 1000);
        for(int i = 0; i < count; ++i) {
            PageInfo pageInfo = new PageInfo(String.valueOf(i), TestUtils.randInt(1000, 2000), TestUtils.randInt(1000, 2000));
            pageInfoList.add(pageInfo);
        }
        return pageInfoList;
    }

    public boolean readMetadata(final ReaderDocumentMetadata metadata) {
        return false;
    }

    public boolean readCover(final Bitmap bitmap) {
        return false;
    }

    private final PageInfo findByName(final String name) {
        for(PageInfo pageInfo: pageInfoList) {
            if (pageInfo.getName().equalsIgnoreCase(name)) {
                return pageInfo;
            }
        }
        return null;
    }

    public RectF getPageOriginSize(final String position) {
        final PageInfo pageInfo = findByName(position);
        if (pageInfo != null) {
            return new RectF(0, 0, pageInfo.getOriginWidth(), pageInfo.getOriginHeight());
        }
        return null;
    }

    @Override
    public boolean supportTextPage() {
        return false;
    }

    @Override
    public boolean isTextPage(String position) {
        return false;
    }

    @Override
    public String getPageText(String position) {
        return "";
    }

    @Override
    public ReaderSentence getSentence(String position, String sentenceStartPosition) {
        return null;
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        return false;
    }

    public ReaderView getView(final ReaderViewOptions viewOptions) {
        return this;
    }

    @Override
    public boolean saveOptions() {
        return true;
    }

    public ReaderViewOptions getViewOptions() {
        return this;
    }

    public ReaderRenderer getRenderer() {
        return null;
    }

    public ReaderNavigator getNavigator() {
        return this;
    }

    public ReaderTextStyleManager getTextStyleManager() {
        return null;
    }

    public ReaderHitTestManager getReaderHitTestManager() {
        return null;
    }

    public ReaderSearchManager getSearchManager() {
        return null;
    }

    public void close() {}

    @Override
    public void updateDocumentOptions(ReaderDocumentOptions documentOptions, ReaderPluginOptions pluginOptions) {

    }

    public boolean supportScale() {
        return true;
    }

    public boolean supportFontSizeAdjustment() {
        return false;
    }

    @Override
    public boolean supportFontGammaAdjustment() {
        return false;
    }

    public boolean supportTypefaceAdjustment() {
        return false;
    }

    @Override
    public boolean supportConvertBetweenSimplifiedAndTraditionalChineseText() {
        return false;
    }

    public String getInitPosition() {
        return firstPage();
    }

    public String getPositionByPageNumber(int pageNumber) {
        return PagePositionUtils.fromPageNumber(pageNumber);
    }

    public String getPositionByPageName(final String pageName) {
        int position = PagePositionUtils.getPageNumber(pageName);
        return PagePositionUtils.fromPageNumber(position);
    }

    @Override
    public int getPageNumberByPosition(String position) {
        return -1;
    }

    public int getTotalPage() {
        return pageInfoList.size();
    }

    @Override
    public int getScreenStartPageNumber() {
        return 0;
    }

    @Override
    public int getScreenEndPageNumber() {
        return 0;
    }

    @Override
    public String getScreenStartPosition() {
        return null;
    }

    @Override
    public String getScreenEndPosition() {
        return null;
    }

    @Override
    public int comparePosition(String pos1, String pos2) {
        return 0;
    }

    @Override
    public boolean gotoPosition(String position) {
        return false;
    }

    @Override
    public boolean gotoPage(int page) {
        return false;
    }

    public String nextScreen(final String position) {
        return nextPage(position);
    }

    public String prevScreen(final String position) {
        return prevPage(position);
    }

    public String nextPage(final String position) {
        int value = PagePositionUtils.getPageNumber(position);
        if (value + 1 < pageInfoList.size()) {
            return PagePositionUtils.fromPageNumber(value + 1);
        }
        return null;
    }

    public String prevPage(final String position) {
        int value = PagePositionUtils.getPageNumber(position);
        if (value - 1 >= 0) {
            return PagePositionUtils.fromPageNumber(value - 1);
        }
        return null;
    }

    @Override
    public boolean isFirstPage() {
        return false;
    }

    public String firstPage() {
        return PagePositionUtils.fromPageNumber(0);
    }

    @Override
    public boolean isLastPage() {
        return false;
    }

    public String lastPage() {
        return PagePositionUtils.fromPageNumber(pageInfoList.size() - 1);
    }

    public List<ReaderSelection> getLinks(final String position) {
        return null;
    }

    @Override
    public List<ReaderImage> getImages(String position) {
        return null;
    }

    @Override
    public ReaderTextStyle getStyle() {
        return null;
    }

    @Override
    public void setStyle(ReaderTextStyle style) {

    }

    public int getViewWidth() {
        return viewport.width();
    }

    public int getViewHeight() {
        return viewport.height();
    }

}
