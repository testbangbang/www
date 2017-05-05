package com.onyx.android.sdk.reader.host.request;

import android.graphics.RectF;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.util.List;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class AddAnnotationRequest extends BaseReaderRequest {

    private PageInfo pageInfo;
    private String locationBegin;
    private String locationEnd;
    private String quote;
    private String note;
    private List<RectF> rects;
    private Annotation annotation;

    public AddAnnotationRequest(PageInfo pageInfo, String locationBegin, String locationEnd,
                                List<RectF> rects, String quote, String note) {
        this.pageInfo = pageInfo;
        this.locationBegin = locationBegin;
        this.locationEnd = locationEnd;
        this.quote = quote;
        this.note = note;
        this.rects = rects;
    }

    public void execute(final Reader reader) throws Exception {
        annotation = createAnnotation(reader);
        DataProviderManager.getLocalDataProvider().addAnnotation(annotation);
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }

    private Annotation createAnnotation(final Reader reader) {
        Annotation annotation = new Annotation();
        annotation.setIdString(reader.getDocumentMd5());
        annotation.setApplication(reader.getPlugin().displayName());
        annotation.setPosition(pageInfo.getPosition());
        annotation.setPageNumber(PagePositionUtils.getPageNumber(pageInfo.getName()));
        annotation.setLocationBegin(locationBegin);
        annotation.setLocationEnd(locationEnd);
        annotation.setQuote(quote);
        annotation.setNote(note);
        annotation.setRectangles(rects);
        return annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
