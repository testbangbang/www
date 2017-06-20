package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.api.ReaderDocumentMetadata;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentMetadataImpl;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ReadDocumentMetadataRequest extends BaseReaderRequest {

    private ReaderDocumentMetadataImpl metadata = new ReaderDocumentMetadataImpl();

    public ReadDocumentMetadataRequest() {
    }

    public void execute(final Reader reader) throws Exception {
        reader.getDocument().readMetadata(metadata);
    }

    public ReaderDocumentMetadata getMetadata() {
        return metadata;
    }
}
