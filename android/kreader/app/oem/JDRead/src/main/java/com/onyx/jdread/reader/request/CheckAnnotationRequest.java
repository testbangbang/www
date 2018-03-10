package com.onyx.jdread.reader.request;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.host.impl.ReaderHitTestOptionsImpl;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;
import com.onyx.jdread.reader.utils.MapKeyComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 2018/3/4.
 */

public class CheckAnnotationRequest extends ReaderBaseRequest {
    private static final String TAG = CheckAnnotationRequest.class.getSimpleName();
    private static final int NOT_NEW_REGION = -1;
    private static final int REGION_EQUALS = 0;
    private static final int NEW_REGION = 1;
    public boolean isEquals = false;
    public String userNote = "";

    private Map<String, SelectionInfo> readerSelectionInfos;
    private List<RegionState> newRegion = new ArrayList<>();
    private RectF pageNewRect;
    List<PageAnnotation> deleteAnnotation;
    private ReaderHitTestOptions hitTestOptions;

    public static class RegionState {
        public int index;
        public int state;
        public RectF newRect;
        public List<PageAnnotation> deleteAnnotation = new ArrayList<>();

        public RegionState(int index, int state, RectF newRect, List<PageAnnotation> deleteAnnotation) {
            this.index = index;
            this.state = state;
            this.newRect = newRect;
            this.deleteAnnotation.addAll(deleteAnnotation);
        }
    }

    public CheckAnnotationRequest(Reader reader, Map<String, SelectionInfo> readerSelectionInfos) {
        super(reader);
        this.readerSelectionInfos = readerSelectionInfos;
        hitTestOptions = ReaderHitTestOptionsImpl.create(false);
    }

    @Override
    public CheckAnnotationRequest call() throws Exception {
        List<Map.Entry<String, SelectionInfo>> list = new ArrayList<Map.Entry<String, SelectionInfo>>(readerSelectionInfos.entrySet());
        Collections.sort(list, new MapKeyComparator());

        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, SelectionInfo> stringStringEntry = list.get(i);
            SelectionInfo readerSelectionInfo = stringStringEntry.getValue();
            ReaderSelection currentSelection = readerSelectionInfo.getCurrentSelection();

            List<PageAnnotation> pageAnnotations = readerSelectionInfo.getPageAnnotations();
            if (pageAnnotations != null) {
                pageNewRect = null;
                deleteAnnotation = null;
                int state = mergePageAnnotation(currentSelection, pageAnnotations);
                if (state != NOT_NEW_REGION) {
                    RegionState regionState = new RegionState(i, state, pageNewRect, deleteAnnotation);
                    newRegion.add(regionState);
                }
            }
        }
        checkMergeResult(list);
        return this;
    }

    private void checkMergeResult(List<Map.Entry<String, SelectionInfo>> list) throws Exception {
        if (newRegion.size() <= 0) {
            return;
        }
        isEquals = true;
        for (RegionState regionState : newRegion) {
            if (regionState.state != REGION_EQUALS) {
                isEquals = false;
                break;
            }
        }
        if (isEquals) {
            return;
        }
        //merge
        String currentPagePosition = getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
        for (RegionState regionState : newRegion) {
            Map.Entry<String, SelectionInfo> stringStringEntry = list.get(regionState.index);
            SelectionInfo readerSelectionInfo = stringStringEntry.getValue();
            ReaderSelection currentSelection = readerSelectionInfo.getCurrentSelection();

            String pagePosition = readerSelectionInfo.getPagePosition();
            getReader().getReaderHelper().getReaderLayoutManager().gotoPosition(pagePosition);

            LayoutProviderUtils.updateReaderViewInfo(getReader(), getReaderViewInfo(), getReader().getReaderHelper().getReaderLayoutManager());

            PageInfo pageInfo = getReaderViewInfo().getPageInfo(pagePosition);

            PointF start = new PointF(regionState.newRect.left, regionState.newRect.top);
            PointF end = new PointF(regionState.newRect.right, regionState.newRect.bottom);

            ReaderHitTestManager hitTestManager = getReader().getReaderHelper().getHitTestManager();

            ReaderHitTestArgs argsStart = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, start);
            ReaderHitTestArgs argsEnd = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, end);

            ReaderSelection selection = hitTestManager.selectOnScreen(argsStart, argsEnd, hitTestOptions);

            if (selection != null && selection.getRectangles().size() > 0) {
                getReaderUserDataInfo().saveHighlightResult(SelectRequest.translateToScreen(pageInfo, selection));
                deleteMergeAnnotation(regionState);
                updateReaderSelectInfo(pagePosition, pageInfo, selection);
            }
        }

        getReader().getReaderHelper().getReaderLayoutManager().gotoPosition(currentPagePosition);

        LayoutProviderUtils.updateReaderViewInfo(getReader(), getReaderViewInfo(), getReader().getReaderHelper().getReaderLayoutManager());
    }

    private void deleteMergeAnnotation(RegionState regionState) {
        List<PageAnnotation> deleteAnnotation = regionState.deleteAnnotation;
        for (PageAnnotation pageAnnotation : deleteAnnotation) {
            Annotation annotation = pageAnnotation.getAnnotation();
            ContentSdkDataUtils.getDataProvider().deleteAnnotation(annotation);
            userNote += pageAnnotation.getAnnotation().getNote();
        }
    }

    private void updateReaderSelectInfo(String pagePosition, PageInfo pageInfo, ReaderSelection readerSelection) {
        SelectionInfo readerSelectionInfo = getReader().getReaderSelectionHelper().getReaderSelectionInfo(pagePosition);
        readerSelectionInfo.setCurrentSelection(readerSelection, pageInfo, null);
    }

    private int mergePageAnnotation(ReaderSelection currentSelection, List<PageAnnotation> pageAnnotations) {
        if (currentSelection.getRectangles().size() <= 0) {
            return NOT_NEW_REGION;
        }
        RectF selectLeftRect = currentSelection.getRectangles().get(0);
        RectF selectRightRect = currentSelection.getRectangles().get(currentSelection.getRectangles().size() - 1);
        float left = -1;
        float top = -1;
        float right = -1;
        float bottom = -1;
        new ArrayList<>();
        deleteAnnotation = new ArrayList<>();
        for (int i = 0; i < pageAnnotations.size(); i++) {
            ArrayList<RectF> rectangles = pageAnnotations.get(i).getRectangles();
            RectF pageLeftRect = null;
            RectF pageRightRect = null;
            for(RectF rect :rectangles) {
                pageLeftRect = rectangles.get(0);
                pageRightRect = rectangles.get(rectangles.size() - 1);

                if (RectUtils.contains(rect,selectLeftRect.left, selectLeftRect.centerY())) {
                    if (left == -1) {
                        left = pageLeftRect.left;
                        top = pageLeftRect.top;
                    }
                    deleteAnnotation.add(pageAnnotations.get(i));
                }
                if (RectUtils.contains(rect,selectRightRect.right, selectRightRect.centerY())) {
                    right = pageRightRect.right;
                    bottom = pageRightRect.bottom;
                    deleteAnnotation.add(pageAnnotations.get(i));
                }
            }
            if(pageLeftRect != null && pageRightRect != null) {
                if(left <= 0 && right <= 0){
                    if (pageLeftRect.top >= selectLeftRect.top &&
                            pageRightRect.top <= selectRightRect.top) {
                        if((pageLeftRect.top == pageLeftRect.top && pageLeftRect.left < selectLeftRect.left) ||
                                pageRightRect.top == selectRightRect.top && pageRightRect.right > selectRightRect.right){

                        }else {
                            if (left == -1) {
                                left = selectLeftRect.left;
                                top = selectLeftRect.top;
                            }
                            right = selectRightRect.right;
                            bottom = selectRightRect.bottom;
                            deleteAnnotation.add(pageAnnotations.get(i));
                        }
                    }
                }
                if (left == selectLeftRect.left && right == selectRightRect.right) {
                    if (left == pageLeftRect.left && right == pageRightRect.right) {
                        return REGION_EQUALS;
                    }
                }
            }
        }
        if (deleteAnnotation.size() > 0) {
            if (left < 0) {
                left = selectLeftRect.left;
                top = selectLeftRect.top;
            }
            if (right < 0) {
                right = selectRightRect.right;
                bottom = selectRightRect.bottom;
            }
            pageNewRect = new RectF(left, top, right, bottom);
            return NEW_REGION;
        }
        return NOT_NEW_REGION;
    }

    public static void printRectangles(List<RectF> rectangles) {
        for (RectF rect : rectangles) {
            Log.e(TAG, rect.toString());
        }
    }
}
