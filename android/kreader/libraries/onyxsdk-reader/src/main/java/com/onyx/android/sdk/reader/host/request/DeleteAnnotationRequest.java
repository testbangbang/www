package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class DeleteAnnotationRequest extends BaseReaderRequest {

    private Annotation annotation;

    public DeleteAnnotationRequest(Annotation annotation) {
        this.annotation = annotation;
    }

    public void execute(final Reader reader) throws Exception {
        DataProviderManager.getLocalDataProvider().deleteAnnotation(annotation);
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
