package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/29.
 */

public class DeleteAnnotationRequest extends ReaderBaseRequest {
    private Annotation annotation;

    public DeleteAnnotationRequest(Reader reader, Annotation annotation) {
        super(reader);
        this.annotation = annotation;
    }

    @Override
    public DeleteAnnotationRequest call() throws Exception {
        ContentSdkDataUtils.getDataProvider().deleteAnnotation(annotation);
        getReader().getReaderViewHelper().updatePageView(getReader(),getReaderUserDataInfo(),getReaderViewInfo());
        updateSetting(getReader());
        return this;
    }
}
