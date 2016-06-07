package com.onyx.kreader.host.request;

import android.graphics.PointF;
import com.onyx.kreader.api.ReaderHitTestArgs;
import com.onyx.kreader.api.ReaderHitTestManager;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;

import java.util.Arrays;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class SelectWordRequest extends BaseReaderRequest {

    private PointF start = new PointF();
    private PointF end = new PointF();
    private ReaderSelection selection;
    private String pageName;

    public SelectWordRequest(final String name, final PointF startPoint, final PointF endPoint) {
        start.set(startPoint.x, startPoint.y);
        end.set(endPoint.x, endPoint.y);
        pageName = name;
    }

    // check page at first. and then goto the location.
    public void execute(final Reader reader) throws Exception {
        createReaderViewInfo();
        ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
        PageInfo pageInfo = reader.getReaderLayoutManager().getPageManager().getPageInfo(pageName);
        ReaderHitTestArgs argsStart = new ReaderHitTestArgs(pageName, pageInfo.getDisplayRect(), 0, start);
        ReaderHitTestArgs argsEnd = new ReaderHitTestArgs(pageName, pageInfo.getDisplayRect(), 0, end);
        selection = hitTestManager.select(argsStart, argsEnd);

        LayoutProviderUtils.updateReaderViewInfo(getReaderViewInfo(), reader.getReaderLayoutManager());
        if (selection != null && selection.getRectangles().size() > 0) {
            getReaderUserDataInfo().saveHighlightResult(translateToScreen(pageInfo, selection));
        }
    }

    private ReaderSelection translateToScreen(PageInfo pageInfo, ReaderSelection selection) {
        for (int i = 0; i < selection.getRectangles().size(); i++) {
            PageUtils.translate(pageInfo.getDisplayRect().left,
                    pageInfo.getDisplayRect().top,
                    pageInfo.getActualScale(),
                    selection.getRectangles().get(i));
        }
        return selection;
    }

}
