package com.onyx.jdread.reader.request;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.HitTestTextHelper;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;
import com.onyx.jdread.reader.highlight.SelectionInfo;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/10.
 */

public class SelectRequest extends ReaderBaseRequest {
    private static final String TAG = SelectRequest.class.getSimpleName();
    private PointF start = new PointF();
    private PointF end = new PointF();
    private PointF touchPoint = new PointF();
    private ReaderSelection selection;
    private String pagePosition;
    private ReaderHitTestOptions hitTestOptions;
    private ReaderSelectionHelper readerSelectionManager;
    private PageInfo pageInfo;
    private List<PageAnnotation> currentPageAnnotation;
    private boolean selectType;

    public SelectRequest(Reader reader, final String pagePosition, final PointF startPoint, final PointF endPoint, final PointF touchPoint,
                         final ReaderHitTestOptions hitTestOptions,PageInfo pageInfo,boolean selectType) {
        super(reader);
        start.set(startPoint.x, startPoint.y);
        end.set(endPoint.x, endPoint.y);
        this.touchPoint.set(touchPoint.x, touchPoint.y);
        this.pagePosition = pagePosition;
        this.hitTestOptions = hitTestOptions;
        this.pageInfo = pageInfo;
        this.selectType = selectType;
    }

    public PointF getstart() {
        return start;
    }

    public PointF getEnd() {
        return end;
    }

    @Override
    public SelectRequest call() throws Exception {
        if (!getReader().getReaderHelper().getReaderLayoutManager().getCurrentLayoutProvider().canHitTest()) {
            LayoutProviderUtils.updateReaderViewInfo(getReader(), getReaderViewInfo(), getReader().getReaderHelper().getReaderLayoutManager());
            return this;
        }
        readerSelectionManager = getReader().getReaderSelectionHelper();
        ReaderHitTestManager hitTestManager = getReader().getReaderHelper().getHitTestManager();
        ReaderHitTestArgs argsStart = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, start);
        ReaderHitTestArgs argsEnd = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, end);
        selection = hitTestManager.selectOnScreen(argsStart, argsEnd, hitTestOptions);
        getReaderViewInfo().setLoadToc(false);
        LayoutProviderUtils.updateReaderViewInfo(getReader(), getReaderViewInfo(), getReader().getReaderHelper().getReaderLayoutManager());
        if (selection != null && selection.getRectangles().size() > 0) {
            getReaderUserDataInfo().saveHighlightResult(translateToScreen(pageInfo, selection));
            getReaderUserDataInfo().setTouchPoint(touchPoint);
            updateReaderSelectInfo(pagePosition,pageInfo);

            HitTestTextHelper.saveLastHighLightPosition(pagePosition,readerSelectionManager,start,end);
        }
        getSelectionInfoManager().updateSelectInfo(readerSelectionManager.getReaderSelectionInfos());
        return this;
    }

    private void updateReaderSelectInfo(String pagePosition,PageInfo pageInfo) {
        SelectionInfo readerSelectionInfo = getReader().getReaderSelectionHelper().getReaderSelectionInfo(pagePosition);
        if(readerSelectionInfo == null || selectType) {
            HitTestTextHelper.loadPageAnnotations(getReader(), getReaderUserDataInfo(), getReaderViewInfo());
            currentPageAnnotation = getReaderUserDataInfo().getPageAnnotations(pageInfo.getName());
        }else{
            currentPageAnnotation = readerSelectionInfo.getPageAnnotations();
        }
        readerSelectionManager.update(pagePosition, getReader().getReaderHelper().getContext(),
                getReaderUserDataInfo().getHighlightResult(),
                getReaderUserDataInfo().getTouchPoint(),
                pageInfo,
                getReader().getReaderHelper().getReaderLayoutManager().getTextStyleManager().getStyle(),
                currentPageAnnotation);
        readerSelectionManager.updateDisplayPosition(pagePosition);
        readerSelectionManager.setEnable(pagePosition, true);
    }

    public static List<PageAnnotation> getPageAnnotations(ReaderViewInfo readerViewInfo,ReaderUserDataInfo readerUserDataInfo) {
        List<PageAnnotation> result = new ArrayList<>();
        for (PageInfo pageInfo : readerViewInfo.getVisiblePages()) {
            if (!readerUserDataInfo.hasPageAnnotations(pageInfo)) {
                continue;
            }

            List<PageAnnotation> annotations = readerUserDataInfo.getPageAnnotations(pageInfo);
            if(annotations != null && annotations.size() > 0){
                result.addAll(annotations);
            }
        }
        return result;
    }

    public static ReaderSelection translateToScreen(PageInfo pageInfo, ReaderSelection selection) {
        int size = selection.getRectangles().size();
        for (int i = 0; i < size; i++) {
            PageUtils.translate(pageInfo.getDisplayRect().left,
                    pageInfo.getDisplayRect().top,
                    pageInfo.getActualScale(),
                    selection.getRectangles().get(i));
        }
        return selection;
    }
}
