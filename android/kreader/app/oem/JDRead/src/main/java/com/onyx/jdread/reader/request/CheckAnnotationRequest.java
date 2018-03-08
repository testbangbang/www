package com.onyx.jdread.reader.request;

import android.graphics.RectF;
import android.util.Log;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderViewHelper;
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
    private Map<String, SelectionInfo> readerSelectionInfos;

    public CheckAnnotationRequest(Reader reader, Map<String, SelectionInfo> readerSelectionInfos) {
        super(reader);
        this.readerSelectionInfos = readerSelectionInfos;
    }

    @Override
    public CheckAnnotationRequest call() throws Exception {
        LayoutProviderUtils.updateReaderViewInfo(getReader(), getReaderViewInfo(), getReader().getReaderHelper().getReaderLayoutManager());
        ReaderViewHelper.loadUserData(getReader(), getReaderUserDataInfo(), getReaderViewInfo());
        List<Map.Entry<String, SelectionInfo>> list = new ArrayList<Map.Entry<String, SelectionInfo>>(readerSelectionInfos.entrySet());
        Collections.sort(list, new MapKeyComparator());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String, SelectionInfo> stringStringEntry = list.get(i);
            SelectionInfo readerSelectionInfo = stringStringEntry.getValue();
            ReaderSelection currentSelection = readerSelectionInfo.getCurrentSelection();

            List<PageAnnotation> pageAnnotations = readerSelectionInfo.getPageAnnotations();
            if (pageAnnotations != null) {
                getPageNewSelectRegion(currentSelection, pageAnnotations);
            }
        }
        return this;
    }

    private void getPageNewSelectRegion(ReaderSelection currentSelection, List<PageAnnotation> pageAnnotations) {
        ArrayList<RectF> result = new ArrayList<>();

        if (currentSelection.getRectangles().size() <= 0) {
            return;
        }
        RectF selectLeftRect = currentSelection.getRectangles().get(0);
        RectF selectRightRect = currentSelection.getRectangles().get(currentSelection.getRectangles().size() - 1);
        float left = -1;
        float top = -1;
        float right = -1;
        float bottom = -1;
        List<PageAnnotation> deleteAnnotation = new ArrayList<>();
        for (int i = 0; i < pageAnnotations.size(); i++) {
            ArrayList<RectF> rectangles = pageAnnotations.get(i).getRectangles();
            if (rectangles.size() > 0) {
                RectF pageLeftRect = rectangles.get(0);
                RectF pageRightRect = rectangles.get(rectangles.size() - 1);

                if (pageLeftRect.contains(selectLeftRect.left, selectLeftRect.centerY())) {
                    if (left == -1) {
                        left = pageLeftRect.left;
                        top = pageLeftRect.top;
                    }
                    deleteAnnotation.add(pageAnnotations.get(i));
                }
                if (pageLeftRect.contains(selectRightRect.right, selectRightRect.centerY())) {
                    right = pageRightRect.right;
                    bottom = pageRightRect.bottom;
                    deleteAnnotation.add(pageAnnotations.get(i));
                }
                if (pageRightRect.contains(selectLeftRect.left, selectLeftRect.centerY())) {
                    if (left == -1) {
                        left = pageLeftRect.left;
                        top = pageLeftRect.top;
                    }
                    deleteAnnotation.add(pageAnnotations.get(i));
                }
                if (pageRightRect.contains(selectRightRect.right, selectRightRect.centerY())) {
                    right = pageRightRect.right;
                    bottom = pageRightRect.bottom;
                    deleteAnnotation.add(pageAnnotations.get(i));
                }
                if (pageLeftRect.top >= selectLeftRect.top && pageRightRect.top <= selectRightRect.top) {
                    if(left == -1) {
                        left = selectLeftRect.left;
                        top = selectLeftRect.top;
                    }
                    right = selectRightRect.right;
                    bottom = selectRightRect.bottom;
                    deleteAnnotation.add(pageAnnotations.get(i));
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
        }
    }

    public static void printRectangles(List<RectF> rectangles) {
        for (RectF rect : rectangles) {
            Log.e(TAG, rect.toString());
        }
    }
}
