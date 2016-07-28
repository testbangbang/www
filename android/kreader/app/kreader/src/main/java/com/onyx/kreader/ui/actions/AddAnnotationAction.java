package com.onyx.kreader.ui.actions;

import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.AddAnnotationRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by joy on 7/7/16.
 */
public class AddAnnotationAction extends BaseAction {

    private PageInfo pageInfo;
    private String locationBegin;
    private String locationEnd;
    private String quote;
    private String note;
    private List<RectF> rects;

    public AddAnnotationAction(PageInfo pageInfo, String locationBegin, String locationEnd,
                               List<RectF> rects, String quote, String note) {
        this.pageInfo = pageInfo;
        this.locationBegin = locationBegin;
        this.locationEnd = locationEnd;
        this.quote = quote;
        this.note = note;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.submitRequest(new AddAnnotationRequest(pageInfo, locationBegin, locationEnd, rects, quote, note));
    }
}
