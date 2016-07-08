package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.dataprovider.Annotation;
import com.onyx.kreader.dataprovider.AnnotationProvider;
import com.onyx.kreader.dataprovider.Bookmark;
import com.onyx.kreader.dataprovider.BookmarkProvider;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class AddAnnotationRequest extends BaseReaderRequest {

    private Annotation annotation;

    public AddAnnotationRequest(Annotation annotation) {
        this.annotation = annotation;
    }

    public void execute(final Reader reader) throws Exception {
        AnnotationProvider.addAnnotation(annotation);
        LayoutProviderUtils.updateReaderViewInfo(createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
