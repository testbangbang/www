package com.onyx.kreader.ui.actions;

import android.graphics.RectF;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.AddAnnotationRequest;
import com.onyx.kreader.ui.ReaderActivity;

import java.util.Date;
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
        this.rects = translateToDocument(rects);
    }

    @Override
    public void execute(ReaderActivity readerActivity) {
        readerActivity.submitRequest(new AddAnnotationRequest(createAnnotation(readerActivity)));
    }

    private Annotation createAnnotation(ReaderActivity readerActivity) {
        Annotation annotation = new Annotation();
        annotation.setMd5(readerActivity.getReader().getDocumentMd5());
        annotation.setApplication(readerActivity.getReader().getPlugin().displayName());
        annotation.setPosition(pageInfo.getName());
        annotation.setLocationBegin(locationBegin);
        annotation.setLocationEnd(locationEnd);
        annotation.setQuote(quote);
        annotation.setNote(note);
        annotation.setRectangles(rects);
        Date time = new Date();
        annotation.setCreatedAt(time);
        annotation.setUpdatedAt(time);
        return annotation;
    }

    private List<RectF> translateToDocument(List<RectF> rects) {
        for (RectF rect : rects) {
            PageUtils.translateToDocument(pageInfo, rect);
        }
        return rects;
    }
}
