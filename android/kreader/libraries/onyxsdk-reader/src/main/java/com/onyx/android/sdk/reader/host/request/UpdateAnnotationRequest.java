package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.dataprovider.ContentSdKDataUtils;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class UpdateAnnotationRequest extends BaseReaderRequest {

    private Annotation annotation;

    public UpdateAnnotationRequest(Annotation annotation) {
        this.annotation = annotation;
    }

    public void execute(final Reader reader) throws Exception {
        ContentSdKDataUtils.getDataProvider().updateAnnotation(annotation);
        LayoutProviderUtils.updateReaderViewInfo(reader, createReaderViewInfo(), reader.getReaderLayoutManager());
    }
}
