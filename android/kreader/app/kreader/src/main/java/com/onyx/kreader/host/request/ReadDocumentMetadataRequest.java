package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderDocumentMetadata;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.impl.ReaderDocumentMetadataImpl;
import com.onyx.kreader.host.wrapper.Reader;

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
