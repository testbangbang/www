package com.onyx.edu.reader.ui.actions;

import android.graphics.RectF;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.request.AddAnnotationRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

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
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        final AddAnnotationRequest addAnnotationRequest = new AddAnnotationRequest(pageInfo, locationBegin, locationEnd, rects, quote, note);
        readerDataHolder.submitNonRenderRequest(addAnnotationRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Annotation annotation = addAnnotationRequest.getAnnotation();
                readerDataHolder.onTextSelected(annotation);
                readerDataHolder.exportAnnotation(annotation);
                BaseCallback.invoke(callback, request, e);
            }
        });
    }

    private List<RectF> translateToDocument(List<RectF> rects) {
        for (RectF rect : rects) {
            PageUtils.translateToDocument(pageInfo, rect);
        }
        return rects;
    }
}
