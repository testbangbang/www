package com.onyx.jdread.reader.request;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.HitTestTextHelper;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class PreviousPageSelectTextRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderTextStyle style;
    private ReaderSelectionHelper readerSelectionManager;
    private String currentPagePosition;
    private String newPagePosition;
    private float width;
    private float height;
    private ReaderSelection currentPageReaderSelect;
    private PointF currentPageTouchPoint;
    private PageInfo pageInfo;

    public PreviousPageSelectTextRequest(Reader reader, ReaderTextStyle style) {
        this.reader = reader;
        this.style = style;
    }

    @Override
    public PreviousPageSelectTextRequest call() throws Exception {
        width = reader.getReaderViewHelper().getContentWidth();
        height = reader.getReaderViewHelper().getContentHeight();
        readerSelectionManager = reader.getReaderSelectionHelper();

        currentPagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();

        if (!extendCurrentPageUpperLeftSelectTextRegion()) {
            isSuccess = false;
            updateSetting(reader);
            return this;
        }

        reader.getReaderHelper().getReaderLayoutManager().prevScreen();
        newPagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();

        SelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(newPagePosition);
        if (readerSelectionInfo == null) {
            PointF start = new PointF(width, height);
            PointF end = new PointF(0,0);
            readerSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, -ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), false, newPagePosition);
            if (readerSelectionInfo == null || readerSelectionInfo.getCurrentSelection().getRectangles().size() <= 0) {
                isSuccess = false;
                reader.getReaderHelper().getReaderLayoutManager().nextScreen();
                updateSetting(reader);
                return this;
            }
            updateReaderSelectInfo(newPagePosition,readerSelectionInfo.pageInfo);
            updateCursorState(newPagePosition,1,false);
            updateCurrentPageReaderSelect();
            reader.getReaderViewHelper().updatePageView(reader, getReaderUserDataInfo(),getReaderViewInfo(), readerSelectionManager);
            HitTestTextHelper.saveLastHighLightPosition(newPagePosition, readerSelectionManager, readerSelectionInfo.getHighLightBeginTop(), readerSelectionInfo.getHighLightEndBottom());
        } else {
            cleanCurrentPageInfo();
        }
        getSelectionInfoManager().updateSelectInfo(readerSelectionManager.getReaderSelectionInfos());
        updateSetting(reader);
        return this;
    }

    private void cleanCurrentPageInfo() {
        readerSelectionManager.deletePageSelection(currentPagePosition);

        SelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(newPagePosition);
        if (readerSelectionInfo != null) {
            PointF start = readerSelectionInfo.getHighLightBeginTop();
            PointF end = readerSelectionInfo.getHighLightEndBottom();
            ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
            PageInfo pageInfo = reader.getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(newPagePosition);
            readerSelectionInfo = HitTestTextHelper.selectOnScreen(start, end, newPagePosition, pageInfo, hitTestManager, getReaderUserDataInfo());
            if (readerSelectionInfo != null && readerSelectionInfo.getCurrentSelection() != null) {
                updateReaderSelectInfo(newPagePosition,pageInfo);
                updateCursorState(newPagePosition,1,true);
                reader.getReaderViewHelper().updatePageView(reader,getReaderUserDataInfo(), getReaderViewInfo(), readerSelectionManager);
            }
        }
    }

    private void updateReaderSelectInfo(String pagePosition,PageInfo pageInfo) {
        readerSelectionManager.update(pagePosition, reader.getReaderHelper().getContext(),
                getReaderUserDataInfo().getHighlightResult(),
                getReaderUserDataInfo().getTouchPoint(),
                pageInfo,
                reader.getReaderHelper().getReaderLayoutManager().getTextStyleManager().getStyle());
        readerSelectionManager.updateDisplayPosition(pagePosition);
        readerSelectionManager.setEnable(pagePosition, true);
    }

    private void updateCursorState(String pagePosition, int index, boolean isShow) {
        readerSelectionManager.getHighlightCursor(pagePosition, index).isShow(isShow);
    }

    private boolean extendCurrentPageUpperLeftSelectTextRegion() {
        SelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(currentPagePosition);

        PointF start = new PointF(0,0);
        PointF end = new PointF(width, height);
        SelectionInfo newReaderSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, ReaderConfig.HIT_TEST_TEXT_STEP, reader, getReaderUserDataInfo(), true, currentPagePosition);
        if (newReaderSelectionInfo != null) {
            readerSelectionInfo.setHighLightBeginTop(newReaderSelectionInfo.getHighLightBeginTop());

            start = readerSelectionInfo.getHighLightBeginTop();
            end = readerSelectionInfo.getHighLightEndBottom();

            ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
            PageInfo pageInfo = reader.getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(currentPagePosition);

            newReaderSelectionInfo = HitTestTextHelper.selectOnScreen(start, end, currentPagePosition, pageInfo, hitTestManager, getReaderUserDataInfo());
            if (newReaderSelectionInfo != null && newReaderSelectionInfo.getCurrentSelection() != null) {
                currentPageReaderSelect = getReaderUserDataInfo().getHighlightResult();
                currentPageTouchPoint = getReaderUserDataInfo().getTouchPoint();
                this.pageInfo = pageInfo;
                return true;
            }
        }
        return false;
    }

    private void updateCurrentPageReaderSelect() {
        readerSelectionManager.update(currentPagePosition, reader.getReaderHelper().getContext(),
                currentPageReaderSelect,
                currentPageTouchPoint,
                pageInfo,
                reader.getReaderHelper().getReaderLayoutManager().getTextStyleManager().getStyle());
        readerSelectionManager.updateDisplayPosition(currentPagePosition);
        readerSelectionManager.setEnable(currentPagePosition, true);
    }
}
