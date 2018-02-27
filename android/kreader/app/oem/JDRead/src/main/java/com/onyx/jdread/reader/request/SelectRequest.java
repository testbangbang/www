package com.onyx.jdread.reader.request;

import android.graphics.PointF;
import android.util.Log;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestArgs;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderHitTestOptions;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.highlight.HitTestTextHelper;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;
import com.onyx.jdread.reader.layout.LayoutProviderUtils;

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
    private Reader reader;
    private ReaderSelectionHelper readerSelectionManager;
    private PageInfo pageInfo;

    public SelectRequest(Reader reader, final String pagePosition, final PointF startPoint, final PointF endPoint, final PointF touchPoint,
                         final ReaderHitTestOptions hitTestOptions,PageInfo pageInfo) {
        start.set(startPoint.x, startPoint.y);
        end.set(endPoint.x, endPoint.y);
        this.touchPoint.set(touchPoint.x, touchPoint.y);
        this.pagePosition = pagePosition;
        this.hitTestOptions = hitTestOptions;
        this.reader = reader;
        this.pageInfo = pageInfo;
    }

    public PointF getstart() {
        return start;
    }

    public PointF getEnd() {
        return end;
    }

    @Override
    public SelectRequest call() throws Exception {
        if (!reader.getReaderHelper().getReaderLayoutManager().getCurrentLayoutProvider().canHitTest()) {
            LayoutProviderUtils.updateReaderViewInfo(reader, getReaderViewInfo(), reader.getReaderHelper().getReaderLayoutManager());
            return this;
        }
        readerSelectionManager = reader.getReaderSelectionHelper();
        ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
        ReaderHitTestArgs argsStart = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, start);
        ReaderHitTestArgs argsEnd = new ReaderHitTestArgs(pagePosition, pageInfo.getDisplayRect(), 0, end);
        selection = hitTestManager.selectOnScreen(argsStart, argsEnd, hitTestOptions);
        getReaderViewInfo().setLoadToc(false);
        LayoutProviderUtils.updateReaderViewInfo(reader, getReaderViewInfo(), reader.getReaderHelper().getReaderLayoutManager());
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
        readerSelectionManager.update(pagePosition, reader.getReaderHelper().getContext(),
                getReaderUserDataInfo().getHighlightResult(),
                getReaderUserDataInfo().getTouchPoint(),
                pageInfo,
                reader.getReaderHelper().getReaderLayoutManager().getTextStyleManager().getStyle());
        readerSelectionManager.updateDisplayPosition(pagePosition);
        readerSelectionManager.setEnable(pagePosition, true);
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
