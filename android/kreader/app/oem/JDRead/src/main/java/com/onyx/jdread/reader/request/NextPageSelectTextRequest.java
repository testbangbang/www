package com.onyx.jdread.reader.request;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.HitTestTextHelper;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.menu.common.ReaderConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class NextPageSelectTextRequest extends ReaderBaseRequest {
    private ReaderTextStyle style;
    private ReaderSelectionHelper readerSelectionManager;
    private String currentPagePosition;
    private String newPagePosition;
    private float width;
    private float height;
    private ReaderSelection currentPageReaderSelect;
    private List<PageAnnotation> currentPageAnnotation = new ArrayList<>();
    private PointF currentPageTouchPoint;
    private PageInfo pageInfo;

    public NextPageSelectTextRequest(Reader reader, ReaderTextStyle style) {
        super(reader);
        this.style = style;
    }

    @Override
    public NextPageSelectTextRequest call() throws Exception {
        width = getReader().getReaderViewHelper().getContentWidth();
        height = getReader().getReaderViewHelper().getContentHeight();
        readerSelectionManager = getReader().getReaderSelectionHelper();
        currentPagePosition = getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
        if (!extendCurrentPageLowerRightSelectTextRegion()) {
            isSuccess = false;
            updateSetting(getReader());
            return this;
        }
        getReader().getReaderHelper().getReaderLayoutManager().nextScreen();
        HitTestTextHelper.loadPageAnnotations(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        newPagePosition = getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
        SelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(newPagePosition);
        if (readerSelectionInfo == null) {
            PointF start = new PointF(0, 0);
            PointF end = new PointF(width, height);
            readerSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, ReaderConfig.HIT_TEST_TEXT_STEP, getReader(), getReaderUserDataInfo(),true, newPagePosition);
            if (readerSelectionInfo == null || readerSelectionInfo.getCurrentSelection().getRectangles().size() <= 0) {
                isSuccess = false;
                getReader().getReaderHelper().getReaderLayoutManager().prevScreen();
                updateSetting(getReader());
                return this;
            }
            updateReaderSelectInfo(newPagePosition,readerSelectionInfo.pageInfo);
            updateCursorState(newPagePosition,0,false);
            updateCurrentPageReaderSelect();
            getReader().getReaderViewHelper().updatePageView(getReader(), getReaderUserDataInfo(),getReaderViewInfo(), readerSelectionManager);
            HitTestTextHelper.saveLastHighLightPosition(newPagePosition, readerSelectionManager, readerSelectionInfo.getHighLightBeginTop(), readerSelectionInfo.getHighLightEndBottom());
        } else {
            cleanCurrentPageInfo();
        }
        getSelectionInfoManager().updateSelectInfo(readerSelectionManager.getReaderSelectionInfos());
        updateSetting(getReader());
        return this;
    }

    private void updateReaderSelectInfo(String pagePosition,PageInfo pageInfo) {
        readerSelectionManager.update(pagePosition, getReader().getReaderHelper().getContext(),
                getReaderUserDataInfo().getHighlightResult(),
                getReaderUserDataInfo().getTouchPoint(),
                pageInfo,
                getReader().getReaderHelper().getReaderLayoutManager().getTextStyleManager().getStyle(),
                SelectRequest.getPageAnnotations(getReaderViewInfo(),getReaderUserDataInfo()));
        readerSelectionManager.updateDisplayPosition(pagePosition);
        readerSelectionManager.setEnable(pagePosition, true);

    }

    private void updateCursorState(String pagePosition, int index, boolean isShow) {
        readerSelectionManager.getHighlightCursor(pagePosition, index).isShow(isShow);
    }

    private void cleanCurrentPageInfo() {
        readerSelectionManager.deletePageSelection(currentPagePosition);

        SelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(newPagePosition);
        if (readerSelectionInfo != null) {
            PointF start = readerSelectionInfo.getHighLightBeginTop();
            PointF end = readerSelectionInfo.getHighLightEndBottom();
            ReaderHitTestManager hitTestManager = getReader().getReaderHelper().getHitTestManager();
            PageInfo pageInfo = getReader().getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(newPagePosition);
            readerSelectionInfo = HitTestTextHelper.selectOnScreen(start, end, newPagePosition, pageInfo, hitTestManager, getReaderUserDataInfo());
            if (readerSelectionInfo != null && readerSelectionInfo.getCurrentSelection() != null) {
                updateReaderSelectInfo(newPagePosition,pageInfo);
                updateCursorState(newPagePosition,0,true);
                getReader().getReaderViewHelper().updatePageView(getReader(), getReaderUserDataInfo(),getReaderViewInfo(), readerSelectionManager);
            }
        }
    }

    private boolean extendCurrentPageLowerRightSelectTextRegion() {
        HitTestTextHelper.loadPageAnnotations(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        SelectionInfo readerSelectionInfo = readerSelectionManager.getReaderSelectionInfo(currentPagePosition);

        PointF start = new PointF(width, height);
        PointF end = new PointF(0,0);
        SelectionInfo newReaderSelectionInfo = HitTestTextHelper.hitTestTextRegion(start, end, -ReaderConfig.HIT_TEST_TEXT_STEP, getReader(), getReaderUserDataInfo(),false, currentPagePosition);
        if (newReaderSelectionInfo != null) {
            readerSelectionInfo.setHighLightEndBottom(newReaderSelectionInfo.getHighLightEndBottom());

            start = readerSelectionInfo.getHighLightBeginTop();
            end = readerSelectionInfo.getHighLightEndBottom();

            ReaderHitTestManager hitTestManager = getReader().getReaderHelper().getHitTestManager();
            PageInfo pageInfo = getReader().getReaderHelper().getReaderLayoutManager().getPageManager().getPageInfo(currentPagePosition);
            newReaderSelectionInfo = HitTestTextHelper.selectOnScreen(start, end, currentPagePosition, pageInfo, hitTestManager, getReaderUserDataInfo());
            if (newReaderSelectionInfo != null && newReaderSelectionInfo.getCurrentSelection() != null) {
                currentPageReaderSelect = getReaderUserDataInfo().getHighlightResult();
                currentPageTouchPoint = getReaderUserDataInfo().getTouchPoint();
                List<PageAnnotation> pageAnnotations = getReaderUserDataInfo().getPageAnnotations(pageInfo.getName());
                if(pageAnnotations != null && pageAnnotations.size() > 0) {
                    for(PageAnnotation pageAnnotation : pageAnnotations) {
                        currentPageAnnotation.add(pageAnnotation.copy());
                    }
                }
                this.pageInfo = pageInfo;
                return true;
            }
        }
        return false;
    }

    private void updateCurrentPageReaderSelect() {
        readerSelectionManager.update(currentPagePosition, getReader().getReaderHelper().getContext(),
                currentPageReaderSelect,
                currentPageTouchPoint,
                pageInfo,
                getReader().getReaderHelper().getReaderLayoutManager().getTextStyleManager().getStyle(),
                currentPageAnnotation);
        readerSelectionManager.updateDisplayPosition(currentPagePosition);
        readerSelectionManager.setEnable(currentPagePosition, true);
    }
}
