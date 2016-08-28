package com.onyx.kreader.host.request;

import com.onyx.android.sdk.dataprovider.model.Annotation;
import com.onyx.android.sdk.dataprovider.DataProviderManager;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class UpdateAnnotationRequest extends BaseReaderRequest {

    private Annotation annotation;

    public UpdateAnnotationRequest(Annotation annotation) {
        this.annotation = annotation;
    }

    public void execute(final Reader reader) throws Exception {
        DataProviderManager.getDataProvider().updateAnnotation(annotation);
        LayoutProviderUtils.updateReaderViewInfo(createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
